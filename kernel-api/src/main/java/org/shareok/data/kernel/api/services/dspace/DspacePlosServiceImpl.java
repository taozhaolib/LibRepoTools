/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.shareok.data.plosdata.PlosApiData;
import org.shareok.data.plosdata.PlosDoiData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
@Service
@Configurable
public class DspacePlosServiceImpl implements DspaceJournalDataService{

    private PlosDoiData pdd;
    private PlosApiData pad;
    
    @Autowired
    public void setPdd(PlosDoiData pdd){
        this.pdd = pdd;
    }
    
    @Autowired
    public void setPad(PlosApiData pad){
        this.pad = pad;
    }
    
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

    @Override
    public String getDspaceJournalLoadingFilesByDoi(String[] dois) {
        return pdd.getDspaceJournalLoadingFilesByDoi(dois);
    }

    @Override
    public String getApiResponseByDatesAffiliate(String startDate, String endDate, String affiliate) {
        return pad.getApiResponseByDatesAffiliate(startDate, endDate, affiliate);
    }

}
