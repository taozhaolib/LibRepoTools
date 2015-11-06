/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.shareok.data.htmlrequest.HtmlRequest;
import org.shareok.data.sagedata.exceptions.EmptyJournalDataException;

/**
 *
 * @author Tao Zhao
 */
public abstract class SageJournalDataProcessorAbstract implements SageJournalDataProcessor {

    protected Map data;
    protected String journalName;
    protected HtmlRequest htmlRequest;

    public Map getData() {
        return data;
    }

    public String getJournalName() {
        return journalName;
    }

    @Override
    public void setData(Map data) {
        this.data = data;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public HtmlRequest getHtmlRequest() {
        return htmlRequest;
    }

    public void setHtmlRequest(HtmlRequest htmlRequest) {
        this.htmlRequest = htmlRequest;
    }

    @Override
    public void getOutput(String fileName) {
        StringBuffer sb = getArticleResponse();
        if(null != sb && sb.length() != 0){
            processArticleResponse();
        }
    }

    @Override
    public String getArticleTitle() {
        String title = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            title = (String)data.get("title");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return title;
    }

    @Override
    public String getArticleVolume() {
        String volume = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            volume = (String)data.get("volume");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return volume;
    }

    @Override
    public String getArticleIssue() {
        String issue = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            issue = (String)data.get("issue");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return issue;
    }

    @Override
    public String getArticlePages() {
        String pages = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            pages = (String)data.get("pages");
            if(null == pages || "".equals(pages)){
                
            }
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return pages;
    }

    @Override
    public String getArticleYear() {
        String year = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            year = (String)data.get("year");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return year;
    }

    @Override
    public String getArticleCitation() {
        String citation = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            citation = (String)data.get("citation");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return citation;
    }

    @Override
    public Date getArticlePubDate() {
        String date = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            date = (String)data.get("pubdate");

            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    @Override
    public String getArticleDoi() {
        String doi = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            doi = (String)data.get("doi");
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(SageJournalDataProcessorAbstract.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return doi;
    }
    
}
