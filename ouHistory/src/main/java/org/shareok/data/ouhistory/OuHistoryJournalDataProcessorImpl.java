/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.ouhistory;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.ouhistory.exceptions.FileAlreadyExistsException;
import org.shareok.data.ouhistory.exceptions.NonCsvFileException;
import org.springframework.beans.factory.annotation.Autowired;
import safbuilder.SAFPackage;

/**
 *
 * @author Tao Zhao
 */
public class OuHistoryJournalDataProcessorImpl implements OuHistoryJournalDataProcessor {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OuHistoryJournalDataProcessorImpl.class);
    
    private OuHistoryJournalData journalData;

    public OuHistoryJournalData getJournalData() {
        return journalData;
    }

    @Autowired
    public void setJournalData(OuHistoryJournalData journalData) {
        this.journalData = journalData;
    }

    @Override
    public String processSafpackage() {
        try{
            String csvPath = journalData.getFilePath();
            String extension = DocumentProcessorUtil.getFileExtension(journalData.getFilePath());
            if(null == extension || !extension.contains("csv")){
                throw new NonCsvFileException("The uploaded file is not a CSV file!");
            }
            SAFPackage safPackageInstance = new SAFPackage();
            safPackageInstance.processMetaPack(csvPath, true);
            String csvDirectoryPath = DocumentProcessorUtil.getFileContainerPath(csvPath);
            File csv = new File(csvPath);
            File safPackage = new File(csvDirectoryPath + File.separator + "SimpleArchiveFormat.zip");
            File newPackage = null;
            if(safPackage.exists()){
                newPackage = new File(csvDirectoryPath + File.separator + DocumentProcessorUtil.getFileNameWithoutExtension(csv.getName()) + ".zip");
                if(!newPackage.exists()){
                    safPackage.renameTo(newPackage);
                }
                else{
                    throw new FileAlreadyExistsException("The zip file of the SAF package already exists!");
                }
            }
            File safPackageFolder = new File(csvDirectoryPath + File.separator + "SimpleArchiveFormat");
            if(safPackageFolder.exists()){
                FileUtils.deleteDirectory(safPackageFolder);
            }
            return (null == newPackage) ? null : newPackage.getAbsolutePath();
            
        } catch (IOException | FileAlreadyExistsException | NonCsvFileException ex) {
            logger.error(ex.getMessage());
        }
        return null;
    }
    
}
