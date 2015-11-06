/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.util.HashMap;
import org.shareok.data.documentProcessor.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Tao Zhao
 */
public class SageDataUtil {
    
    public static HashMap getJournalListWithBeans(){
        
        HashMap journalMap = new HashMap<String, String>();
        try{
            String journalXml = FileUtil.getFilePathFromResources("sageJournals.xml");
            Document journalXmlDoc = FileUtil.loadXMLFromString(journalXml);
            journalXmlDoc.getDocumentElement().normalize();
            Element docEle = journalXmlDoc.getDocumentElement();
            NodeList nl = docEle.getChildNodes();

            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) nl.item(i);
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
}
