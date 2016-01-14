/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata.journalDataProcessors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.shareok.data.htmlrequest.HtmlParser;
import org.shareok.data.sagedata.SageJournalDataProcessorAbstract;
import org.shareok.data.sagedata.exceptions.EmptyJournalDataException;
import org.shareok.data.sagedata.exceptions.NoArticleIssueException;
import org.shareok.data.sagedata.exceptions.NoArticlePagesException;
import org.shareok.data.sagedata.exceptions.NoArticleVolumeException;

/**
 *
 * @author Tao Zhao
 */
public class ProcInstMechEngProcessor extends SageJournalDataProcessorAbstract {

    public ProcInstMechEngProcessor(String journalName){
        this.journalName = journalName;
    }
    
    @Override
    public String getArticleUrl() {
        
        String url = null;
        try {
            if(null == data)
                throw new EmptyJournalDataException("Journal data is empty!");
            url = (String)data.get("url");
            if(null == url || "".equals(url)){
                String pages = getArticlePages();
                String divider = "";
                
                if(null == pages || "".equals(pages))
                    throw new NoArticlePagesException("No article pages are set up!");
                if(pages.contains("-"))
                    divider = "-";
                else if(pages.contains("~"))
                    divider = "~";
                if(!"".equals(divider)){
                    pages = pages.split(divider)[0];
                }
                
                String volume = getArticleVolume();
                if(null == volume || "".equals(pages))
                    throw new NoArticleVolumeException("No article volume is set up!");
                
                String issue = getArticleIssue();
                if(null == issue || "".equals(issue))
                    throw new NoArticleIssueException("No article issue is set up!");
                
                url = "http://pio.sagepub.com/content/"+volume+"/"+issue+"/"+pages+".abstract";                
            }
            
        } catch (EmptyJournalDataException ex) {
            Logger.getLogger(ProcInstMechEngProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoArticlePagesException ex) {
            Logger.getLogger(ProcInstMechEngProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoArticleVolumeException ex) {
            Logger.getLogger(ProcInstMechEngProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoArticleIssueException ex) {
            Logger.getLogger(ProcInstMechEngProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return url;
    }

    @Override
    public StringBuffer getArticleResponse(){ 
        StringBuffer response = null;
        String url = getArticleUrl();
        try {
            response = getHtmlRequest().sendPost(url);
        } catch (Exception ex) {
            Logger.getLogger(ProcInstMechEngProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
    @Override
    public void processArticleResponse(String html){
        String[] tagNames = {"meta"};
        HashMap<String,ArrayList<String>> metaData = HtmlParser.metaDataParserWithTagNames(html, tagNames);
        Iterator it = metaData.entrySet().iterator(); 
        String authors = "";
                
        try{
            while(it.hasNext()){
                Map.Entry pairs = (Map.Entry)it.next();
                if(pairs.getKey().equals("DC.Contributor")){
                    authors += pairs.getValue()+",";
                }
            }
            if(!authors.equals("")){
                authors = authors.substring(0, authors.length()-1);
                // Remove the "[" and "]" within the string:
                authors = authors.replace("[", "");
                authors = authors.replace("]", "");
                data.put("authors", authors);
            }
        }
        catch(Exception ex){
            Logger.getLogger(ProcInstMechEngProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String abstracts = getArticleAbstract(html);
        if(null != abstracts){
            data.put("abstract", abstracts);
        }
        
        String[] subjects = getArticleSubjects(html);
        String subjectsStr = "";
        if(null != subjects && subjects.length > 0){            
            StringBuilder sb = new StringBuilder();
            for(String s : subjects) {
                sb.append(s+",");
            }
            sb.setLength(sb.length()-1);
            subjectsStr = sb.toString();
        }
        data.put("subjects", subjectsStr);
    }
    
    @Override
    public void setProcessorId() {
        String doi = (String)data.get("doi").toString().replace("/", ".");
        setId(doi);
    }
    
    @Override
    public String getArticleAbstract(String html) {
        String ab = super.getArticleAbstract(html);
        if(null == ab){
            String[] abstracts = HtmlParser.metaDataParserWithElementProperty(html, "p", "id", "p-3");
            if(null != abstracts && abstracts.length > 0){
                ab = abstracts[0];
            }
        }
        return ab;
    } 
}
