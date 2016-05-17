/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.lawlibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.shareok.data.documentProcessor.CsvHandler;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.lawlibrary.exceptions.DateReformatException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class LawLibDataHandlerImpl implements LawLibDataHandler{
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LawLibDataHandlerImpl.class);
    
    public static final String[] COULUMNS_TO_BE_DELETED = {"Maps", "Fold-Out Charts", "collection name (repeatable)", "Special Collection (repeatable)", "rights (repeatable)", "media_type (repeatable)", "file_format (repeatable)"};
    public static final Map <String, String> COLUMN_NAME_MAP_METADATA_SCHEMA;
    static{
        COLUMN_NAME_MAP_METADATA_SCHEMA = new HashMap<>();
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Document Title", "dc.title");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Congress-Session", "dc.description.congressSession");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Number of pages", "dcterms.extent");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Official treaty name (title-alternative)", "dcterms.alternative.treaty");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Serial Set Id", "dc.identifier");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Document Date", "dcterms.issued");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Committee", "dc.description.committee");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Descriptive Title", "dcterms.alternative.title");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Cross Reference - Other House or Senate", "dc.description.otherSerialSetID");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Johnson's Bib reference", "dc.description.johnsonReference");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Johnson Annotation", "dc.description.johnsonAnnotation");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("Notes (hidden)", "dc.description.notes");
        COLUMN_NAME_MAP_METADATA_SCHEMA.put("file_location", "filename");
    }
    //csv.setFileName("/Users/zhao0677/Projects/law-library/pdf/load-test.csv");
        
    private CsvHandler csv;
    private String inputFilePath;
    private String outputFilePath;
    private String outputCsvFilePath;
    private List<String> pdfFileList = new ArrayList<>();
    private List<String> matchedPdfFileList = new ArrayList<>();
    private Map data;

    public CsvHandler getCsv() {
        return csv;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public List<String> getPdfFileList() {
        return pdfFileList;
    }

    public List<String> getMatchedPdfFileList() {
        return matchedPdfFileList;
    }
    
    @Override
    public Map getData() {
        return data;       
    }

    public String getOutputCsvFilePath() {
        return outputCsvFilePath;
    }

    @Autowired
    public void setCsv(CsvHandler csv) {
        this.csv = csv;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public void setPdfFileList(List<String> pdfFileList) {
        this.pdfFileList = pdfFileList;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public void setOutputCsvFilePath(String outputCsvFilePath) {
        this.outputCsvFilePath = outputCsvFilePath;
    }

    public void setMatchedPdfFileList(List<String> matchedPdfFileList) {
        this.matchedPdfFileList = matchedPdfFileList;
    }
    
    @Override
    public void readSourceData() {
        if(null == data){
            if(null == csv.getFileName() || "".equals(csv.getFileName())){
                csv.setFileName(inputFilePath);
            }
            csv.readData();
            data = csv.getData(); 
        }
        cleanData();
    }


    @Override
    public void outputMetaData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * The data provided by the Law Library have some problems:
     *  1. The PDF file names do NOT always match file names listed in the CSV file
     *  2. The file extension names are inconsistent in terms of capitalization
     *  3. Format the existing dates to be ISO-8601 format
     *  4. Remove some columns in the data file as they either have no data or are not recognized by the metadata schema
     *  5. Translate the columns into correct metadata schema
     *  6. When the item has no title, use the dcterms.alternative as dc.title
     *  7. Change the column name File_location to be filename for the SAFBuilder
     * 
     * The solutions:
     *  1. Match the file names in the CSV file with the existing files in the folder
     *  2. All the file names have extension like .pdf
     *  4. Remove the unnecessary columns
     *  5. Convert the normal columns into correct metadata schema
     *  6. Cope the data to the column of dc.title and clear this column
     *  7. Just change it
     *  
     */
    private void cleanData(){
        if(null == data){
            csv.readData();
            data = csv.getData();
        }
        try{
            File pdfFolder = new File(outputFilePath);
            File[] pdfFlist = pdfFolder.listFiles();
            for(File file : pdfFlist){
                String name = file.getName();
                String parent = file.getParent();
                if(file.getPath().toLowerCase().endsWith(".pdf")){                
                    String nameWithoutExtension = FileUtil.getFileNameWithoutExtension(name);
                    if(nameWithoutExtension.toLowerCase().endsWith(".pdf")){
                        nameWithoutExtension = nameWithoutExtension.replaceAll(".pdf", "");
                        nameWithoutExtension = nameWithoutExtension.replaceAll(".PDF", "");
                    }
                    pdfFileList.add(nameWithoutExtension + ".pdf");
                    file.renameTo(new File(parent + File.separator + nameWithoutExtension + ".pdf"));
                    //file.renameTo(new File(".PDF"));
                }
            }

            // Remove the unused columns
            csv.deleteColumnByColumnName(COULUMNS_TO_BE_DELETED);

            // Match the pdf files with the file names in the csv file
            // Only keep the data records that have the PDF files
            // Also update the column names to the metadata schema
            // At the same time, update the date format of the records
            Map<String, String> cleanData = new HashMap<>();        
            String newKey = "";
            String value = "";
            int newRecordCount = 0;
            for(int i = 1; i < csv.getRecordCount(); i++){
                String key = "file_location-" + String.valueOf(i);
                String csvFileName = FileUtil.getFileNameWithoutExtension((String)data.get(key));
                if(csvFileName.toLowerCase().endsWith(".pdf")){
                    csvFileName = csvFileName.replace(".pdf", "");
                    csvFileName = csvFileName.replace(".PDF", "");
                }                
                csvFileName += ".pdf";
                data.put("file_location-" + String.valueOf(i), csvFileName);
                if(pdfFileList.contains(csvFileName)){
                    matchedPdfFileList.add(csvFileName);
                    newRecordCount++;
                    for(String column : csv.getFileHeadMapping()){
                        if(null == column || column.equals("")){
                            continue;
                        }
                        else {
                            column = column.trim();
                            key = column + "-" + String.valueOf(i);
                            value = (String)data.get(key);   
//                            if(null == value || "null" == value){
//                                System.out.println("null value for paper "+csvFileName+" with column "+column);
//                            }
                            String dcTerm = (String)(COLUMN_NAME_MAP_METADATA_SCHEMA.get(column));
                            newKey = dcTerm + "-" + String.valueOf(i);
                            if(column.equals("Document Date")){
                                value = changeDataFormat(value);
                            }
                            else if(column.contains("Document Title") && value.contains("Document not titled")){
                                value = (String)data.get("Official treaty name (title-alternative)-" + String.valueOf(i));
                                data.put("Official treaty name (title-alternative)-" + String.valueOf(i), "");
                            }
                            cleanData.put(newKey, value);
                        }

                    }
                }
            }
            setData(cleanData);
            csv.setData((HashMap)cleanData);
            String[] newHeadingsArray = COLUMN_NAME_MAP_METADATA_SCHEMA.values().toArray(new String[COLUMN_NAME_MAP_METADATA_SCHEMA.values().size()]);
            csv.setFileHeadMapping(newHeadingsArray);
            csv.setRecordCount(newRecordCount);
            outputCsvFilePath = csv.outputData(outputFilePath + File.separator + "metadata.csv");
            FileUtil.outputStringToFile(String.join("\n", matchedPdfFileList), new File(outputFilePath).getPath() + File.separator + "matchedPdfFiles.txt");
        }
        catch(Exception ex){
            logger.error("Cannot clean up the data.", ex);
        }
    }
    
    // The existing date format is mm-dd-yyyy, now changes it to be yyyy-mm-dd
    private String changeDataFormat(String date){
        try{
            String[] dateInfo = date.split("-");
            if(null == dateInfo || dateInfo.length != 3){
                throw new DateReformatException("The date is not in the form of mm-dd-yyyy");
            }
            return dateInfo[2] + "-" + dateInfo[0] + "-" + dateInfo[1];
        }
        catch(DateReformatException ex){
            logger.error("Cannot reformat the date", ex);
        }
        return null;
    }
}
