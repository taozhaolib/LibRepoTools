/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import java.util.Date;
import org.shareok.data.sagedata.SageApiDataHandler;
import org.shareok.data.sagedata.SageSourceDataHandler;
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
public class DspaceSageServiceImpl implements DspaceJournalDataService {
    
    private SageSourceDataHandler ssd;
    private SageApiDataHandler sad;
    
    @Autowired
    public void setSageSourceDataHandler(SageSourceDataHandler ssd){
        this.ssd = ssd;
    }
    
    @Autowired
    public void setSageApiDataHandler(SageApiDataHandler sad){
        this.sad = sad;
    }
    
    /**
     *
     * @param filePath
     * @return String filePath: the path to the folder containing files to loaded into DSpace
     */
//    @Override
//    public String getSageDsapceLoadingFilesByExcel(String filePath){
//        ssd.getDspaceLoadingData(filePath);
//        return filePath;
//    }
    
    /**
     *
     * @param file
     * @param filePath
     * @return String filePath: the path to the folder containing the metadata files
     */
//    @Override
//    public String getSageMetadataFilesByExcel(String filePath){
//        return filePath;
//    }
    
    @Override
    public String getDsapceJournalLoadingFiles(MultipartFile file){
        String filePath = null;
        filePath = ssd.getDspaceLoadingData(file);
        return filePath;
    }
    
    @Override
    public String getDsapceJournalLoadingFiles(String userFilePath){
        String filePath = ssd.getDspaceLoadingData(userFilePath);
        return filePath;
    }

    @Override
    public String getDspaceJournalLoadingFilesByDoi(String[] dois, Date time) {
        return ssd.getDspaceJournalLoadingFilesByDoi(dois, time);
    }

    @Override
    public String getApiResponseByDatesAffiliate(String startDate, String endDate, String affiliate) {
        return sad.getApiResponseByDatesAffiliate(startDate, endDate, affiliate);
    }
}
