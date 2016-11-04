/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.shareok.data.ouhistory.OuHistoryJournalDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class DspaceOuHistoryJournalDataServiceImpl implements DspaceSafPackageDataService {
    
    private OuHistoryJournalDataHandler ohHandler;

    public OuHistoryJournalDataHandler getOuHistoryJournalDataHandler() {
        return ohHandler;
    }
    
    @Autowired
    public void setOuHistoryJournalDataHandler(OuHistoryJournalDataHandler ohHandler){
        this.ohHandler = ohHandler;
    }

    @Override
    public String[] getSafPackagePaths(String dataFolderPath) {
        return ohHandler.processSafPackages(dataFolderPath);
    }

    @Override
    public String[] getSafPackagePaths(MultipartFile file) {
        return ohHandler.processSafPackages(file);
    }
}
