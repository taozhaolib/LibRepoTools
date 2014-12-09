/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosonedata;

import java.util.Date;
import java.util.List;

/**
 * note: dc.identifier.uri will be generated after the articles are imported
 * @author Tao Zhao
 */
public class PlosOneData {
    
    private static final String peerreviewnotes = "http://www.plosone.org/static/editorial#peer";   
    private static final String languageIso = "en_US";                                               
    private static final String publisher = "PLOS ONE";                                              
    private static final String peerreview = "Yes";                                                  
    private static final String rights = "Attribution 3.0 United States";                            
    private static final String rightsUri = "http://creativecommons.org/licenses/by/3.0/us/";        
    private static final boolean rightsRequestable = false;                                          

    private String doi;                                                                             
    private String uri;                                                                             
    private String type;                                                                            
    private String title;                                                                           
    private String abstractText;                                                                    
    private String acknowledgements;                                                               
    private String ispartofseries;                                                                  
    
    private Date dateAvailable;
    private Date dateIssued;
    
    private List<String> authors;                                                                  
    private List<String> subjects;                                                                  
    private List<String> citations;                                                                 
    
    /**
     *
     * @return dc.description.peerreviewnotes
     */
    public static String getPeerreviewnotes() {
        return peerreviewnotes;
    }

    /**
     *
     * @return dc.language.iso;
     */
    public static String getLanguageIso() {
        return languageIso;
    }

    /**
     *
     * @return dc.publisher
     */
    public static String getPublisher() {
        return publisher;
    }

    /**
     *
     * @return dc.description.peerreview
     */
    public static String getPeerreview() {
        return peerreview;
    }

    /**
     *
     * @return dc.rights 
     */
    public static String getRights() {
        return rights;
    }

    /**
     *
     * @return dc.relation.uri
     */
    public static String getRightsUri() {
        return rightsUri;
    }

    /**
     *
     * @return dc.rights.requestable
     */
    public static boolean isRightsRequestable() {
        return rightsRequestable;
    }

    /**
     *
     * @return dc.identifier.doi
     */
    public String getDoi() {
        return doi;
    }

    /**
     *
     * @return dc.identifier.uri 
     */
    public String getUri() {
        return uri;
    }

    /**
     *
     * @return dc.type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return dc.title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return dc.description.abstract
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     *
     * @return dc.description
     */
    public String getAcknowledgements() {
        return acknowledgements;
    }

    /**
     *
     * @return dc.relation.ispartofseries
     */
    public String getIspartofseries() {
        return ispartofseries;
    }

    /**
     *
     * @return dc.date
     */
    public Date getDateAvailable() {
        return dateAvailable;
    }

    /**
     *
     * @return dc.date
     */
    public Date getDateIssued() {
        return dateIssued;
    }

    /**
     *
     * @return dc.contributor.author
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     *
     * @return dc.subject
     */
    public List<String> getSubjects() {
        return subjects;
    }

    /**
     *
     * @return dc.identifier.citation
     */
    public List<String> getCitations() {
        return citations;
    }

    /**
     *
     * @param doi
     */
    public void setDoi(String doi) {
        this.doi = doi;
    }

    /**
     *
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param abstractText
     */
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    /**
     *
     * @param acknowledgements
     */
    public void setAcknowledgements(String acknowledgements) {
        this.acknowledgements = acknowledgements;
    }

    /**
     *
     * @param ispartofseries
     */
    public void setIspartofseries(String ispartofseries) {
        this.ispartofseries = ispartofseries;
    }

    /**
     *
     * @param dateAvailable
     */
    public void setDateAvailable(Date dateAvailable) {
        this.dateAvailable = dateAvailable;
    }

    /**
     *
     * @param dateIssued
     */
    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    /**
     *
     * @param authors
     */
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    /**
     *
     * @param subjects
     */
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    /**
     *
     * @param citations
     */
    public void setCitations(List<String> citations) {
        this.citations = citations;
    }
    
        
    public void getDataFromExcel(String fileName) {
        
      
    }
}
