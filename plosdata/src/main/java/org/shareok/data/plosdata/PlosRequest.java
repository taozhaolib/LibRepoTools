/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.shareok.data.datahandlers.DataHandlersUtil;
import org.shareok.data.htmlrequest.HttpRequestHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tao Zhao
 */
public class PlosRequest {
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    private HttpRequestHandler htmlRequest;
    
    /**
     * 
     * @param doiInfo: string contain the article doi information, such as 10.1371/journal.pone.0041479
     * @return some of the metadata provided by the plos api
     */
    public String getMetaDataByApi(String doiInfo) {
       
       if(null == doiInfo || "".equals(doiInfo)){
           return "";
       }
       
       String data = null;              
        try {
            String api_key = DataHandlersUtil.getPublisherApiKeyByName("plos");
            String link = PlosUtil.API_SEARCH_PREFIX + doiInfo + "&&api_key=" + api_key;
            StringBuffer temp = getHtmlRequest().sendPost(link);
            data = temp.toString();
        } catch (Exception ex) {
            Logger.getLogger(PlosRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
    
    public String getRelationUriByDoi(String doiInfo) {
        if(null == doiInfo || "".equals(doiInfo)){
           return "";
       }
        
       String prefix = "";
       if(doiInfo.contains("pone"))
            prefix = PlosUtil.URL_FULLTEXT_PREFIX_PONE;
       else if(doiInfo.contains("pbio"))
            prefix = PlosUtil.URL_FULLTEXT_PREFIX_PBIO;
       else if(doiInfo.contains("pcbi"))
            prefix = PlosUtil.URL_FULLTEXT_PREFIX_PCBI;
       else if(doiInfo.contains("pmed"))
            prefix = PlosUtil.URL_FULLTEXT_PREFIX_PMED;
       else if(doiInfo.contains("pgen"))
            prefix = PlosUtil.URL_FULLTEXT_PREFIX_PGEN;
       else if(doiInfo.contains("pntd"))
            prefix = PlosUtil.URL_FULLTEXT_PREFIX_PNTD;
       else if(doiInfo.contains("ppat"))
            prefix = PlosUtil.URL_FULLTEXT_PREFIX_PPAT;
       
       String encodedDoiInfo = doiInfo.replace("/", "%2F");
       String link = prefix + encodedDoiInfo;
       
       return link;
    }
    
    /**
     * 
     * @param doiInfo: string contain the article doi information, such as 10.1371/journal.pone.0041479
     * @return the full text plus the metadata
     */
    public String getFullData(String doiInfo) {
       
       String data = "";
       String link = getRelationUriByDoi(doiInfo);
       
       try {
            StringBuffer temp = getHtmlRequest().sendPost(link);
            data = temp.toString();
        } catch (Exception ex) {
            Logger.getLogger(PlosRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
 
//    public String getImportedDataPath(String doi) {
//        
//        String path = "";
//        try{
//            
//            String resource = PlosRequest.class.getName().replace(".", File.separator) + ".class";      
//            URL fileURL = ClassLoader.getSystemClassLoader().getResource(resource);
//            path = new File(fileURL.toURI()).getParent();
//            String folderName = doi.split("/")[1];
//            path = path + File.separator + "importedData" + File.separator + folderName;
//        }
//        catch(URISyntaxException ex){
//            ex.printStackTrace();
//        }
//        return path;
//    }

    public void downloadPlosOnePdfByDoi(String doi, String outputFolderPath) {
        
        try{
            boolean directoryExists = false;
            File newDir = new File(outputFolderPath);
            if(newDir.exists()){
                directoryExists = true;
                System.out.println("The directory: " + newDir + " has already exists!\n");
            }
            else{
                if(!newDir.mkdirs()){
                    System.out.println("Directory creation failed!");
                }
                else{
                    directoryExists = true;
                }
            }
            if(directoryExists){
                //The example url to downlaod: http://dx.plos.org/10.1371/journal.pone.0041479.pdf
                String pdfUrl = PlosUtil.getPlosPDFUrl(doi);
                getHtmlRequest().getPdfByUrl(pdfUrl, newDir + File.separator + doi.split("/")[1] + ".pdf");
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public String getPlosTitle(String doi) {
        String title = "";
        
        return title;
    }
    
    public String getUSER_AGENT() {
        return USER_AGENT;
    }

    public HttpRequestHandler getHtmlRequest() {
        return htmlRequest;
    }

    public void setHtmlRequest(HttpRequestHandler htmlRequest) {
        this.htmlRequest = htmlRequest;
    }
    
}
