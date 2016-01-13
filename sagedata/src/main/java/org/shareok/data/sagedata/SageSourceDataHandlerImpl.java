/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.shareok.data.documentProcessor.FileHandler;
import org.shareok.data.documentProcessor.FileHandlerFactory;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.sagedata.exceptions.EmptyFilePathException;

/**
 *
 * @author Tao Zhao
 */
public class SageSourceDataHandlerImpl implements SageSourceDataHandler{
    
    private String sourceFilePath;
    private String outputFilePath;
    private HashMap data;
    private SageJournalDataProcessorFactory factory;
    private ArrayList<HashMap> itemData;

    /**
     *
     * @return
     */
    @Override
    public HashMap getData() {
        return data;
    }

    /**
     *
     * @param data
     */
    public void setData(HashMap data) {
        this.data = data;
    }

    /**
     *
     * @return
     */
    public ArrayList<HashMap> getItemData() {
        return itemData;
    }

    /**
     *
     * @param itemData
     */
    public void setItemData(ArrayList<HashMap> itemData) {
        this.itemData = itemData;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public SageJournalDataProcessorFactory getFactory() {
        return factory;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public void setFactory(SageJournalDataProcessorFactory factory) {
        this.factory = factory;
    }

    /**
     *
     * @param filePath
     */
    @Override
    public void readSourceData() {
        
        String filePath = sourceFilePath;
        try{
            String fileExtension = FileUtil.getFileExtension(filePath);
            FileHandler fh = FileHandlerFactory.getFileHandlerByFileExtension(fileExtension);
            if(null == fh)
                return;
            fh.setFileName(filePath);
            fh.readData();
            data = fh.getData();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * Organize the raw data in order to retrieve the necessary information to request the metadata
     * Note: this method is closely depending on the excel file format
     */
    @Override
    public void processSourceData() {
        
        if(null == data || data.isEmpty()){
            readSourceData();
            if(null == data || data.isEmpty())
                return;
        }
        
        try{
            Set keys = data.keySet();
            Iterator it = keys.iterator();
            int rowPre = 0;

            HashMap articleData = new HashMap();
            
            while(it.hasNext()){
                String key = (String)it.next();
                String value = (String)data.get(key);
                // the values is composed of "val--datatype": for example, Tom--Str or 0.50--num
                String[] values = value.split("--");
                if(null == values || values.length != 2)
                    continue;

                value = values[0];
                String[] rowCol = key.split("-");
                if(null == rowCol || rowCol.length != 2)
                    throw new Exception("The row and column are not specifid!");
                int row = Integer.parseInt(rowCol[0]);
                int col = Integer.parseInt(rowCol[1]);
                
                if(row != rowPre){
                    rowPre = row;
                    if(null != articleData && !articleData.isEmpty()){
                        if(null == itemData){
                            itemData = new ArrayList<HashMap>();
                        }
                        Object articleDataCopy = articleData.clone();
                        itemData.add((HashMap)articleDataCopy);
                        articleData.clear();
                    }                    
                }
                
                if(0 != row){
                    switch(col){
                        case 0:
                            articleData.put("journal", value);
                            break;
                        case 2:
                            articleData.put("title", value);
                            break;
                        case 3:
                            articleData.put("volume", value);
                            break;
                        case 4:
                            articleData.put("issue", value);
                            break;
                        case 5:
                            articleData.put("pages", value);
                            break;
                        case 6:
                            articleData.put("year", value);
                            break;
                        case 7:
                            articleData.put("citation", value);
                            break;
                        case 8:
                            articleData.put("pubdate", value);
                            break;
                        case 9:
                            articleData.put("doi", value);
                            break;
                        case 10:
                            articleData.put("url", value);
                            break;
                        default:
                            break;
                    }
                }
                
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    
    }
    
    public void outputMetaData(){
        
        String filePath = outputFilePath;
        
        try{
            if(null == filePath || "".equals(filePath)){
                throw new EmptyFilePathException("File path is NOT set!");
            }
            if(null == itemData || itemData.isEmpty()){
                processSourceData();
                if(null == itemData || itemData.isEmpty())
                    return;
            }
            File outputFolder = new File(filePath);
            if(!outputFolder.exists()){
                if(outputFolder.mkdir()){
                    System.out.print("The folder for data loading has been created.\n");
                }
            }
            
            int size = itemData.size();
            for(int i = 0; i < size; i++){
                Map journalData = itemData.get(i);
                String journal = (String)journalData.get("journal");
                Map journalMap = SageDataUtil.getJournalListWithBeans();
                SageJournalDataProcessor sjdp = SageJournalDataProcessorFactory.getSageJournalDataProcessorByName(journalMap, journal);
                sjdp.setData(journalData);
                if(null == sjdp){
                    System.out.print("The No. "+i+" article has no metadata ...");
                    continue;
                }
                else{
                    sjdp.getOutput(filePath);
                    System.out.print("The No. "+i+" article metadata has been prepared...");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}