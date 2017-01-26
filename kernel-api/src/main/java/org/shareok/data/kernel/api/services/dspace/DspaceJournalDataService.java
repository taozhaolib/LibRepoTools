/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceJournalDataService {
    public String getDsapceJournalLoadingFiles(MultipartFile file);
    public String getDsapceJournalLoadingFiles(String userFilePath);
    public String getDspaceJournalLoadingFilesByDoi(String[] dois);
}
