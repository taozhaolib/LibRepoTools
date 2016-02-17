/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api;

import org.shareok.data.kernel.api.services.dspace.DspaceJournalServiceManager;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        String publisher = args[0];
        String filePath = args[1];
        DspaceJournalServiceManager.getDspaceJournalDataService(publisher).getDsapceJournalLoadingFiles(filePath);
    }
}
