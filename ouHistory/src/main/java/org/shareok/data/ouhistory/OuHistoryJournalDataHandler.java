/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.ouhistory;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public interface OuHistoryJournalDataHandler {
    public String[] processSafPackages(String dataPath);
    public String[] processSafPackages(MultipartFile file);
}
