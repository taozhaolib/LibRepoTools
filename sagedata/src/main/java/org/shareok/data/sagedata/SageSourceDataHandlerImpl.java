/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.shareok.data.documentProcessor.FileHandler;
import org.shareok.data.documentProcessor.FileHandlerFactory;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.sagedata.exceptions.EmptyFilePathException;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.documentProcessor.FileZipper;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class SageSourceDataHandlerImpl implements SageSourceDataHandler {

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
        try {
            String fileExtension = FileUtil.getFileExtension(filePath);
            FileHandler fh = FileHandlerFactory.getFileHandlerByFileExtension(fileExtension);
            if (null == fh) {
                return;
            }
            fh.setFileName(filePath);
            fh.readData();
            data = fh.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Organize the raw data in order to retrieve the necessary information to
     * request the metadata Note: this method is closely depending on the excel
     * file format
     */
    @Override
    public void processSourceData() {

        if (null == data || data.isEmpty()) {
            readSourceData();
            if (null == data || data.isEmpty()) {
                return;
            }
        }

        try {
            Set keys = data.keySet();
            Iterator it = keys.iterator();
            int rowPre = 0;

            HashMap articleData = new HashMap();

            while (it.hasNext()) {
                String key = (String) it.next();
                String value = (String) data.get(key);
                // the values is composed of "val--datatype": for example, Tom--Str or 0.50--num
                String[] values = value.split("--");
                if (null == values || values.length != 2) {
                    continue;
                }

                value = values[0];
                String[] rowCol = key.split("-");
                if (null == rowCol || rowCol.length != 2) {
                    throw new Exception("The row and column are not specifid!");
                }
                int row = Integer.parseInt(rowCol[0]);
                int col = Integer.parseInt(rowCol[1]);

                if (row != rowPre) {
                    rowPre = row;
                    if (null != articleData && !articleData.isEmpty()) {
                        if (null == itemData) {
                            itemData = new ArrayList<HashMap>();
                        }
                        Object articleDataCopy = articleData.clone();
                        itemData.add((HashMap) articleDataCopy);
                        articleData.clear();
                    }
                }

                if (0 != row) {
                    switch (col) {
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
            
            // Put the last article into itemData:
            if (null != articleData && !articleData.isEmpty()) {
                if (null == itemData) {
                    itemData = new ArrayList<HashMap>();
                }
                Object articleDataCopy = articleData.clone();
                itemData.add((HashMap) articleDataCopy);
                articleData.clear();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void outputMetaData() {

        String filePath = outputFilePath;

        try {
            if (null == filePath || "".equals(filePath)) {
                throw new EmptyFilePathException("File path is NOT set!");
            }
            if (null == itemData || itemData.isEmpty()) {
                processSourceData();
                if (null == itemData || itemData.isEmpty()) {
                    return;
                }
            }
            File outputFolder = new File(filePath);
            if (!outputFolder.exists()) {
                if (outputFolder.mkdir()) {
                    System.out.print("The folder for data loading has been created.\n");
                }
            }

            int size = itemData.size();
            for (int i = 0; i < size; i++) {
                Map journalData = itemData.get(i);
                String journal = (String) journalData.get("journal");
                Map journalMap = SageDataUtil.getJournalListWithBeans();
                SageJournalDataProcessor sjdp = SageJournalDataProcessorFactory.getSageJournalDataProcessorByName(journalMap, journal);

                if (null == sjdp) {
                    System.out.print("The No. " + i + " article from journal \" " + journal + " \" has no metadata ...\n");
                    continue;
                } else {
                    sjdp.setData(journalData);
                    sjdp.getOutput(filePath);
                    System.out.print("The No. " + i + " article metadata has been prepared...\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String getDspaceLoadingData(String filePath){
        setSourceFilePath(filePath);
        setOutputFilePath(filePath);
        readSourceData();
        processSourceData();
        outputMetaData();
        return filePath;
    }
    
    @Override
    public String saveUploadedData(MultipartFile file){
        String uploadedFilePath = null;
        try{
            String oldFileName = file.getOriginalFilename();
            String extension = FileUtil.getFileExtension(oldFileName);
            oldFileName = FileUtil.getFileNameWithoutExtension(oldFileName);
            //In the future the new file name will also has the user name
            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String newFileName = oldFileName + "--" + time + "." + extension;
            String uploadPath = ShareokdataManager.getSageUploadPath();
            if(null != uploadPath){
                File uploadFolder = new File(uploadPath);
                if(!uploadFolder.exists()){
                    uploadFolder.mkdir();
                }
                File uploadTimeFolder = new File(uploadPath + File.separator + time);
                if(!uploadTimeFolder.exists()){
                    uploadTimeFolder.mkdir();
                }
            }
            uploadedFilePath = uploadPath + File.separator + time + File.separator + newFileName;
            File uploadedFile = new File(uploadedFilePath);
            file.transferTo(uploadedFile);
        }
        catch(Exception ex){
            Logger.getLogger(SageSourceDataHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uploadedFilePath;
    }
    
    @Override
    public String getDspaceLoadingData(MultipartFile file){
        String filePath = null;
        try {
            filePath = saveUploadedData(file);
            if(null != filePath){
                setSourceFilePath(filePath);
                setOutputFilePath(FileUtil.getFileContainerPath(filePath) + "output");
                readSourceData();
                processSourceData();
                outputMetaData();
                packLoadingData();
            }            
        } catch (Exception ex) {
            Logger.getLogger(SageSourceDataHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return filePath;
    }
    
    private String packLoadingData(){
        String zipPath = null;
        String outputFolder = getOutputFilePath();
        
        try{
            zipPath = FileUtil.getFileContainerPath(outputFolder) + File.separator + "output.zip";
            FileZipper.zipFolder(outputFolder, zipPath);
        }
        catch(Exception ex){
            Logger.getLogger(SageSourceDataHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return zipPath;
    }
}
