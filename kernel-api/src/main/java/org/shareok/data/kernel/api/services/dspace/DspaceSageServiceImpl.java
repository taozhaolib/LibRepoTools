/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import java.io.File;
import org.shareok.data.sagedata.SageSourceDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class DspaceSageServiceImpl implements DspaceSageService{
    
    @Autowired
    private SageSourceDataHandler ssd;

//    /**
//     *  get SageSourceDataHandler
//     * @return SageSourceDataHandler ssd
//     */
//    public SageSourceDataHandler getSsd() {
//        return ssd;
//    }
//
//    /**
//     * set SageSourceDataHandler
//     * @param SageSourceDataHandler ssd
//     */
//    public void setSsd(SageSourceDataHandler ssd) {
//        this.ssd = ssd;
//    }
    
    /**
     *
     * @param filePath
     * @return String filePath: the path to the folder containing files to loaded into DSpace
     */
    @Override
    public String getSageDsapceLoadingFilesByExcel(String filePath){
        ssd.getDspaceLoadingData(filePath);
        return filePath;
    }
    
    /**
     *
     * @param filePath
     * @return String filePath: the path to the folder containing the metadata files
     */
    @Override
    public String getSageMetadataFilesByExcel(String filePath){
        return filePath;
    }
    
    @Override
    public String getSageDsapceLoadingFiles(MultipartFile file){
        String filePath = null;
        filePath = ssd.getDspaceLoadingData(file);
        return filePath;
    }
}
