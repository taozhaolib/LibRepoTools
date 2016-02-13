/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.shareok.data.config.ShareokdataManager;

/**
 *
 * @author Tao Zhao
 */
public class WebservService {
    
    /**
     * @param filePath String : in a form of : /shareokdata/uploads/sage/2016.02.03.14.35.08/sagedata--2016.02.03.14.35.08.xlsx
     * @return Map downloadLinks : the download links to the original uploaded file and the output zip file for DSpace loading
     */
    public static Map getDspaceDownloadLinks(String filePath){
        Map downloadLinks = new HashMap<String, String>();
        String[] filePathInfo = filePath.split("/");
        int length = filePathInfo.length;
        
        String folderPath = filePathInfo[length-3] + File.separator + filePathInfo[length-2] + File.separator;
        String oldFileLink = File.separator + "webserv" + File.separator + "download" + File.separator + folderPath + filePathInfo[length-1] + File.separator;
        downloadLinks.put("oldFile", oldFileLink);
        
        String loadingFileLink = File.separator + "webserv" + File.separator + "download" + File.separator + folderPath + "output.zip" + File.separator;
        downloadLinks.put("loadingFile", loadingFileLink);
        
        return downloadLinks;
    }
    
    /**
     *
     * @param publisher : e.g. sage or plos
     * @param folderName : the folder generated after the user uploads the data file
     * @param fileName : the file name generated after the data are processed
     * @return downloadPath : the file path for downloading
     */
    public static String getDspaceDownloadFilePath(String publisher, String folderName, String fileName){
        String downloadPath = null;
        String uploadPathFunction = ShareokdataManager.getUploadPathFunction(publisher);
        
        return downloadPath;
    }
}
