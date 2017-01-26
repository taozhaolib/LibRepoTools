/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.plosdata;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public interface PlosDoiData {
    public void getDspaceLoadingData(String fileName) throws Exception;
    public String getDspaceLoadingData(MultipartFile file);
    public String getDspaceJournalLoadingFilesBySingleDoi(String doi);
    public String getDspaceJournalLoadingFilesByDoi(String[] dois);
//    public String saveUploadedData(MultipartFile file);
}
