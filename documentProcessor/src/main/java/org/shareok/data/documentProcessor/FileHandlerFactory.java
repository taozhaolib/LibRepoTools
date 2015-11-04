/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.documentProcessor;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.shareok.data.documentProcessor.FileUtil.loadXMLFromString;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Tao Zhao
 */
public class FileHandlerFactory {
    /**
     *
     * @param fileExtension
     * @return
     */
    public static FileHandler getFileHandlerByFileExtension(String fileExtension) {
        
        FileHandler fh = null;
        String beanName = "";
        
        try {
            String fileTypePath = FileUtil.getFilePathFromResources("filetypes.xml");
            Document fileTypeDoc = loadXMLFromString(fileTypePath);
            fileTypeDoc.getDocumentElement().normalize();
            Element docEle = fileTypeDoc.getDocumentElement();
            NodeList nl = docEle.getChildNodes();
            
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) nl.item(i);
                        String nodeVal = el.getTextContent();
                        if(nodeVal.equals(fileExtension)){
                            beanName = el.getAttribute("bean");
                            break;
                        }
                    }
                }
            }
            
            ApplicationContext context = new ClassPathXmlApplicationContext("documentProcessorContext.xml");
            fh = (FileHandler) context.getBean(beanName);
            
        } catch (Exception ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return fh;
    }
}
