/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Tao Zhao
 */
public class FileUtil {
    public static String getFilePathFromResources(String fileName) {
        String path = null;
        try{
            URL url = FileUtil.class.getClassLoader().getResource(fileName);
            path = url.getPath();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return path;
    }
    
    public static File getFileFromResources(String fileName) {
        String path = getFilePathFromResources(fileName);
        File file = new File(path);
        return file;
    }
    
    public static String getFileExtension(String fileName) {

        if(null != fileName && !"".equals(fileName)) {
            String[] nameFragments = fileName.split("\\.(?=[^\\.]+$)");
            return nameFragments[nameFragments.length-1];
//            String[] nameFragments = fileName.split("/");
//            int length = nameFragments.length;
//            if(length > 0) {
//                fileName = nameFragments[length-1];
//                nameFragments = fileName.split(".");
//                return nameFragments[nameFragments.length-1];
//            }
//            else {
//                return "";
//            }
        }
        else {
            return "";
        }
    }
    
    public static String[] getDataFromXmlByTagName(String filePath, String tagName) {
        String[] data = null;
        try {
            File file = new File(filePath);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName(tagName);
            
            int length = nList.getLength();
            
            if(length == 0){
                return null;
            }

            List<String> dataList = new ArrayList<>();
            for (int temp = 0; temp < length; temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element eElement = (Element) nNode;
                            dataList.add(eElement.getTextContent());
                            //System.out.println("file type : " + eElement.getTextContent() + "\n");

                    }
            }
            
            int size = dataList.size();
            data = new String[size];
            data = dataList.toArray(data);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}
