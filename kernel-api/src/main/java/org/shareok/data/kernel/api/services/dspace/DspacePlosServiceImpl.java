/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.shareok.data.plosdata.PlosDoiData;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class DspacePlosServiceImpl implements DspacePlosService{
    
    @Autowired
    private PlosDoiData pdd;
    
    @Override
    public String getPlosDsapceLoadingFilesByExcel(String filePath){
        return filePath;
    }
    
    @Override
    public String getPlosMetadataFilesByExcel(String filePath){
        return filePath;
    }
}
