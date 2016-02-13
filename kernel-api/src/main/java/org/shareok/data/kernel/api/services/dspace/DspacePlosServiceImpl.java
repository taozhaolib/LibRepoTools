/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.shareok.data.plosdata.PlosDoiData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class DspacePlosServiceImpl implements DspaceJournalDataService{
    
    @Autowired
    private PlosDoiData pdd;
    
    @Override
    public String getDsapceJournalLoadingFiles(MultipartFile file){
        String filePath = null;
        filePath = pdd.getDspaceLoadingData(file);
        return filePath;
    }
    
    @Override
    public String getDsapceJournalLoadingFiles(String userFilePath){
        return null;
    }

}
