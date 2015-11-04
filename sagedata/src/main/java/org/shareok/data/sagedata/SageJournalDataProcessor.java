/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

/**
 *
 * @author Tao Zhao
 */
public interface SageJournalDataProcessor {
    public String getArticleUrl();
    public StringBuffer getArticleResponse();
    public void getOutput(String fileName);
}
