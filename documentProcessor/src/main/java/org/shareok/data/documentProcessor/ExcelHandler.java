/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.udf.IndexedUDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.shareok.data.documentProcessor.exceptions.*;

/**
 * Handles Excel files
 * @author Tao Zhao
 */
public class ExcelHandler implements FileHandler {
      
    private String fileName;
    private FileRouter router;
    private HashMap data;

    public FileRouter getRouter() {
        return router;
    }

    @Override
    public HashMap getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setRouter(FileRouter router) {
        this.router = router;
    }

    public void setData(HashMap data) {
        this.data = data;
    }
    
    /**
     * Based on the file extension to create corresponding workbook object and
     * return the Sheet object
     * 
     * @param extension : file extension name of the excel file
     * @param file : FileInputStream
     * @return Sheet object
     * @throws IOException : IO exception handler
     * 
     */
    private Sheet getWorkbookSheet(String extension, FileInputStream file) throws IOException {
        Sheet sheet = null;
        if("xlsx".equals(extension)){
            XSSFWorkbook workbook = new XSSFWorkbook(file);workbook.setMissingCellPolicy(HSSFRow.RETURN_NULL_AND_BLANK);
            sheet = workbook.getSheetAt(0);
        }
        if("xls".equals(extension)){
            HSSFWorkbook workbook = new HSSFWorkbook(file);workbook.setMissingCellPolicy(HSSFRow.RETURN_NULL_AND_BLANK);
            sheet = workbook.getSheetAt(0);
        }
        return sheet;
    }
    
    /**
     * Check if the cells are date type
     * 
     * @param cell
     * @return : bool
     * @throws Exception 
     */
    private boolean isCellDateFormatted(Cell cell) throws Exception {
        try{
            return DateUtil.isCellDateFormatted(cell);
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception ("The cell type data formatted cannot be decided!");
        }
    }
    
    /**
     * Reads out the data in an excel file and stores data in a hashmap<p>
     * The cell data has the ending of "--type" to label the data type
     * 
     * @throws Exception
     */
    @Override
    public void readData() {
        
        String name = fileName;
        Sheet sheet = null;
        
        try {
            if(null == name || "".equals(name)) {
                throw new FileNameException("File name is not specified!"); 
            }
            
            FileInputStream file = new FileInputStream(new File(name));

            String extension = DocumentProcessorUtil.getFileExtension(name);

            String[] excelTypes = router.loadOfficeFileType("excel");
            
            if(null == excelTypes || excelTypes.length == 0){
                throw new FileTypeException("The file types are empty!");
            }

            HashMap<String,String> typeMap = new HashMap<>();
            for(String s : excelTypes){
                typeMap.put(s, s);
            }

            if(typeMap.containsKey(extension)){
                if(extension.equals("xlsx")){
    
                }
            }

            sheet = getWorkbookSheet(extension, file);
            int maxNumOfCells = sheet.getRow(0).getLastCellNum();
            Iterator<Row> rowIterator = sheet.iterator();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            int rowCount = 0;
            //int colCount = 0;
            
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                //while(cellIterator.hasNext()) {
                for(int colCount = 0; colCount < maxNumOfCells; colCount++) {

                    //Cell cell = cellIterator.next();
                    Cell cell = row.getCell(colCount);
                    if(null == cell){
                        cell = row.createCell(colCount);
                    }
                    String key = Integer.toString(rowCount) + "-" + Integer.toString(colCount);
                    switch(cell.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            data.put(key, Boolean.toString(cell.getBooleanCellValue()) + "---bool");
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if(isCellDateFormatted(cell)) {
                                data.put(key, df.format(cell.getDateCellValue()) + "---dat");
                            }
                            else{
                                data.put(key, Double.toString(cell.getNumericCellValue()) + "---num");
                            }
                            break;
                        case Cell.CELL_TYPE_STRING:
                            data.put(key, cell.getStringCellValue() + "---str");
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            data.put(key, "");
                            break;
                        case Cell.CELL_TYPE_ERROR:                        
                            data.put(key, "ERROR_VALUE");
                            break;
                        case Cell.CELL_TYPE_FORMULA:                                
                            FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
                            //handleCell(cell.getCachedFormulaResultType(), cell, evaluator);
                            data.put(key, String.valueOf(cell.getCachedFormulaResultType()));
                            break;
                        default:
                            data.put(key, cell.getRichStringCellValue() + "---def");
                            break;
                    }              
                //    colCount++;
                }
                rowCount++;
                //colCount = 0;
            }
            file.close();
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcelHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ExcelHandler.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    /**
     * Export data to an Xml file
     * 
     * @param map
     * @param filePath 
     */
    @Override
    public void exportMapDataToXml(HashMap map, String filePath) {
        try{
            Iterator it = map.keySet().iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                Object obj = (Object)map.get(key);
                if(obj instanceof ArrayList){
                    
                }
                else if(obj instanceof String){

                }
                else{
                    System.out.println("Undefined data type");
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ExcelHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
