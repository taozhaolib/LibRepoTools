/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public interface SageJournalDataProcessor {
    public String getArticleUrl();
    public String getArticleTitle();
    public String getArticleVolume();
    public String getArticleIssue();
    public String getArticlePages();
    public String getArticleYear();
    public String getArticleCitation();
    public Date getArticlePubDate();
    public String getArticleDoi();
    public StringBuffer getArticleResponse();
    
    public void setData(Map data);
    
    public void processArticleResponse(String html);
    public void getOutput(String fileName);
    
    public String getArticleAbstract(String html);
    public String[] getArticleSubjects(String html);
    
    public void exportXmlByJournalData(String fileName);
    public void convertDataToJournalData();
    
    public void setProcessorId();
    public String getFullTextLink();
}
