/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.documentProcessor.FileZipper;
import org.shareok.data.ssh.SshExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class DspaceJournalDataUtil {
    
    /**
     * 
     * @param file : the uploaded file
     * @param publisher : journal publisher, e.g. sage or plos
     * @return : the path of the uploading folder
     */
    public static String saveUploadedData(MultipartFile file, String publisher){
        String uploadedFilePath = null;
        try{
            String oldFileName = file.getOriginalFilename();
            String extension = FileUtil.getFileExtension(oldFileName);
            oldFileName = FileUtil.getFileNameWithoutExtension(oldFileName);
            //In the future the new file name will also has the user name
            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String newFileName = oldFileName + "--" + time + "." + extension;
            String uploadPath = getDspaceJournalUploadFolderPath(publisher);
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
            Logger.getLogger(DspaceJournalDataUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uploadedFilePath;
    }
    
    /**
     * @param filePath String : in a form of : /shareokdata/uploads/sage/2016.02.03.14.35.08/sagedata--2016.02.03.14.35.08.xlsx
     * @return Map downloadLinks : the download links to the original uploaded file and the output zip file for DSpace loading
     */
    public static Map getDspaceJournalDownloadLinks(String filePath){
        Map downloadLinks = new HashMap<String, String>();
        String[] filePathInfo = filePath.split("/");
        int length = filePathInfo.length;
        
        String folderPath = filePathInfo[length-3] + File.separator + filePathInfo[length-2] + File.separator;
        String oldFileLink = File.separator + "webserv" + File.separator + "download" + File.separator + "dspace" + File.separator + "journal" + File.separator + folderPath + filePathInfo[length-1] + File.separator;
        downloadLinks.put("oldFile", oldFileLink);
        
        String loadingFileLink = File.separator + "webserv" + File.separator + "download" + File.separator + "dspace" + File.separator + "journal" + File.separator + folderPath + "output.zip" + File.separator;
        downloadLinks.put("loadingFile", loadingFileLink);
        
        return downloadLinks;
    }
    
    /**
     *
     * @param publisher : e.g. sage or plos
     * @return downloadPath : the file path for downloading
     */
    public static String getDspaceJournalUploadFolderPath(String publisher){
        String uploadFolderPath = null;
        Class noparams[] = {};
        try{
            String uploadPathFunction = ShareokdataManager.getUploadPathFunction(publisher);
            Method method = ShareokdataManager.class.getMethod(uploadPathFunction, noparams);
            uploadFolderPath = (String)method.invoke(null, (Object[])noparams);
        }
        catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex){
            Logger.getLogger(DspaceJournalDataUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uploadFolderPath;
    }
    
    /**
     *
     * @param publisher : e.g. sage or plos
     * @param folderName : the folder generated after the user uploads the data file
     * @param fileName : the file name generated after the data are processed
     * @return downloadPath : the file path for downloading
     */
    public static String getDspaceJournalDownloadFilePath(String publisher, String folderName, String fileName){
        
        String downloadPath = null;
        Class noparams[] = {};
        try{
            String uploadPathFunction = ShareokdataManager.getUploadPathFunction(publisher);
            Method method = ShareokdataManager.class.getMethod(uploadPathFunction, noparams);
            downloadPath = (String)method.invoke(null, (Object[])noparams) + File.separator + folderName + File.separator + fileName;
        }
        catch(NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex){
            Logger.getLogger(DspaceJournalDataUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return downloadPath;
    }
    
    
    public static String packLoadingData(String outputFolder){
        
        String zipPath = null;
        try{
            zipPath = FileUtil.getFileContainerPath(outputFolder) + File.separator + "output.zip";
            FileZipper.zipFolder(outputFolder, zipPath);
        }
        catch(Exception ex){
            Logger.getLogger(DspaceJournalDataUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return zipPath;
    }
    
    /**
     * Sample file path: now it is hard coded but should be configurable in the future.
     * @return link : the link to download the sample dublin core xml file
     */
    public static String getJournalSampleDublinCoreLink(){
        return File.separator + "webserv" + File.separator + "download" + File.separator + "dspace" + File.separator + "journal" + File.separator + "sampleDC.xml";
    }
    
    public static String getJournalImportFilePath(String uploadFileName, String publisher){
        return ShareokdataManager.getShareokdataPath() + File.separator + "uploads" + File.separator +
                publisher + File.separator + uploadFileName;
    }
    
}
