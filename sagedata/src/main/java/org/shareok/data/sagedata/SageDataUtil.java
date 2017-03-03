/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.util.HashMap;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shareok.data.datahandlers.exceptions.NoHtmlComponentsFoundException;

/**
 *
 * @author Tao Zhao
 */
public class SageDataUtil {
    
    public static final String SAGE_HTTP_PREFIX = "http://journals.sagepub.com";
    public static final String SAGE_FULL_TEXT_HTTP_PREFIX = "http://journals.sagepub.com/doi/full/";
    public static final String SAGE_PDF_HTTP_PREFIX = "http://journals.sagepub.com/doi/pdf/";
    public static final String API_SEARCH_PREFIX = "http://journals.sagepub.com/action/doSearch?";
    
    public static HashMap getJournalListWithBeans(){
        
        HashMap journalMap = new HashMap<String, String>();
        try{
            String journalXml = DocumentProcessorUtil.getFilePathFromResources("sageJournals.xml");
            org.w3c.dom.Document journalXmlDoc = DocumentProcessorUtil.loadXMLFromString(journalXml);
            journalXmlDoc.getDocumentElement().normalize();
            org.w3c.dom.Element docEle = journalXmlDoc.getDocumentElement();
            NodeList nl = docEle.getChildNodes();

            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        org.w3c.dom.Element el = (org.w3c.dom.Element) nl.item(i);
                        String nodeVal = el.getTextContent();
                        String bean = el.getAttribute("bean");
                        journalMap.put(nodeVal, bean);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return journalMap;
    }
    
    public static String getArticleUrlByDoi(String doi){
        return SAGE_FULL_TEXT_HTTP_PREFIX + "10.1177/1477971416672328";
    }
    
    public static boolean hasFullAccess(Document doc) throws NoHtmlComponentsFoundException{
        Elements iconElements = doc.select("img.accessIcon");
        if(null == iconElements || iconElements.isEmpty()){
            throw new NoHtmlComponentsFoundException("Cannot find the icon image elements with 'img.accessIcon' ");
        }
        Element iconElement = iconElements.get(0);
        String iconImgTitle = iconElement.attr("title");
        if(null == iconImgTitle || iconImgTitle.equals("")){
            throw new NoHtmlComponentsFoundException("Cannot find the icon image title");
        }
        if(iconImgTitle.equals("Free Access") || iconImgTitle.equals("Full Access")){
            return true;
        }
        else {
            return false;
        }
    }
    
    public static String getPdfLinkFromDoi(String doi){
        return SAGE_PDF_HTTP_PREFIX + doi;
    }
}
