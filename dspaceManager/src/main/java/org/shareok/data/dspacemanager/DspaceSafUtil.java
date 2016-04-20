/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.documentProcessor.FileUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class DspaceSafUtil {
    /**
     * Save the uploaded SAF file at specified path 
     * 
     * @param file : the uploaded file
     * @return : the path of the uploading folder
     */
    public static String saveUploadedData(MultipartFile file){
        return FileUtil.saveMultipartFileByTimePath(file, ShareokdataManager.getSafUploadPath());
    }
}
