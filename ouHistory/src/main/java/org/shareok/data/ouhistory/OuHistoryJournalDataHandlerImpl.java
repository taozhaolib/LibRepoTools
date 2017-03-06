/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.ouhistory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.documentProcessor.FileZipper;
import org.shareok.data.documentProcessor.exceptions.FileTypeException;
import org.shareok.data.dspacemanager.DspaceJournalDataUtil;
import org.shareok.data.ouhistory.exceptions.IncorrectDataFolderException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class OuHistoryJournalDataHandlerImpl implements OuHistoryJournalDataHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OuHistoryJournalDataHandlerImpl.class);

    @Override
    public String[] processSafPackages(String dataPath) {
        String[] safPackagePaths = null;
        List<String> safPackagePathList = new ArrayList<>();
        try{
            OuHistoryJournalDataProcessorImpl processor = (OuHistoryJournalDataProcessorImpl)OuHistoryJournalDataUtil.getOuHistoryJournalDataProcessorInstance();
            File dataFolder = new File(dataPath);
            if(!dataFolder.exists() || !dataFolder.isDirectory()){
                throw new IncorrectDataFolderException("The uploaded data folder does NOT exist or is NOT a directory!");
            }
            FilenameFilter fileNameFilter = new FilenameFilter() {   
                @Override
                public boolean accept(File dir, String name) {
                   if(name.lastIndexOf('.')>0)
                   {
                      int lastIndex = name.lastIndexOf('.');
                      String str = name.substring(lastIndex);
                      if(str.equals(".csv"))
                      {
                         return true;
                      }
                   }
                   return false;
                }
             };
            OuHistoryJournalData data = OuHistoryJournalDataUtil.getOuHistoryJournalDataInstance();
            for(File file : dataFolder.listFiles(fileNameFilter)){                
                data.setFilePath(file.getAbsolutePath());
                processor.setJournalData(data);
                String safPath = processor.processSafpackage();
                if(!DocumentProcessorUtil.isEmptyString(safPath)){
                    safPackagePathList.add(safPath);
                }
            }
            safPackagePaths = safPackagePathList.toArray(new String[safPackagePathList.size()]);
        }
        catch(IncorrectDataFolderException ex){
            logger.error(ex.getMessage());
        }        
        return safPackagePaths;
    }
    
    @Override
    public String[] processSafPackages(MultipartFile file) {
        String[] safPackagePaths = null;
        try {
            String filePath = DspaceJournalDataUtil.saveUploadedData(file, "ouhistory");
            if(null != filePath){
                String extension = DocumentProcessorUtil.getFileExtension(filePath);
                if(null == extension || !extension.contains("zip")){
                    throw new FileTypeException("The uploaded file is NOT a zip file");
                }
                String dataFolderPath = FileZipper.unzipToDirectory(filePath);
                safPackagePaths = processSafPackages(dataFolderPath);
            }            
        } catch (FileTypeException ex) {
            logger.error(ex.getMessage());
        }        
        return safPackagePaths;
    }
    
}
