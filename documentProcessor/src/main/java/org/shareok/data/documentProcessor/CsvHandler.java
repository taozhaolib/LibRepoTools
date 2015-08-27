/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.documentProcessor;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Tao Zhao
 */
public class CsvHandler implements FileHandler {
    private String fileName;
    private HashMap data;
    private int recordCount;
    private String [] fileHeadMapping;

    public String getFileName() {
        return fileName;
    }

    public HashMap getData() {
        return data;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setData(HashMap data) {
        this.data = data;
    }

    public String[] getFileHeadMapping() {
        return fileHeadMapping;
    }

    public void setFileHeadMapping(String[] fileHeadMapping) {
        this.fileHeadMapping = fileHeadMapping;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    
    /**
     * Reads out the data in an excel file and stores data in a hashmap
     * <p>Also sets the total record number and file heading</p>
     * 
     * @throws Exception
     */
    @Override
    public void readData() {
        FileReader fileReader = null;
        CSVParser csvFileParser = null;
        String[] headMapping = null;
        //CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        
        try {            
            //initialize FileReader object
            fileReader = new FileReader(fileName);

            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, CSVFormat.DEFAULT);
            
            //Get a list of CSV file records
            List csvRecords = csvFileParser.getRecords(); 
            
            int size = csvRecords.size();
            
            setRecordCount(size);

            //Read the CSV file records starting from the second record to skip the header
            for (int i = 0; i < size; i++) {
                CSVRecord record = (CSVRecord)csvRecords.get(i);   
                if(null != record){
                    if(i == 0){
                        List headMappingList = new ArrayList();
                        Iterator it = record.iterator();
                        while(it.hasNext()){
                            String value = (String)it.next();
                            headMappingList.add(value);
                        }                    
                        headMapping = new String[headMappingList.size()];
                        headMapping = (String[]) headMappingList.toArray(headMapping);
                        setFileHeadMapping(headMapping);                        
                    }
                    else{
                        for(int j = 0; j < fileHeadMapping.length; j++){
                            String colName = fileHeadMapping[j];
                            String key = colName + "-" + i;
                            data.put(key, record.get(j));
                        }
                    }
                }
            }

        } 
        catch (Exception e) {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void exportMapDataToXml(HashMap map, String filePath) {
        
    }
}
