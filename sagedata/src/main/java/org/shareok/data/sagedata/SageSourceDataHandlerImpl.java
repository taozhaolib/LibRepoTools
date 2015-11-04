/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

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
        
        if(null == data){
            readSourceData();
            if(null == data)
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
                        if(null == itemData)
                            itemData = new ArrayList<HashMap>();
                        itemData.add(articleData);
                    }
                    articleData.clear();
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
                throw new EmptyFilePathException();
            }
            if(null == itemData){
                processSourceData();
                if(null == itemData)
                    return;
            }
            
            int size = itemData.size();
            for(int i = 0; i < size; i++){
                
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}