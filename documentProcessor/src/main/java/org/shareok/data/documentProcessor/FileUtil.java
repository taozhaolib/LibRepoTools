/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    /**
     * Get the path of the file in the resources folder 
     * 
     * @param fileName
     * @return String path
     */
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
    
    /**
     * Get the File object of the file in the resources folder
     * 
     * @param fileName
     * @return File file
     */
    public static File getFileFromResources(String fileName) {
        String path = getFilePathFromResources(fileName);
        File file = new File(path);
        return file;
    }
    
    /**
     *
     * @param fileName
     * @return
     */
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
    
    /**
     *
     * @param fileName
     * @return
     */
    public static String getFileNameWithoutExtension(String fileName) {

        if(null != fileName && !"".equals(fileName)) {
            String[] nameFragments = fileName.split("\\.(?=[^\\.]+$)");
            return nameFragments[0];
        }
        else {
            return "";
        }
    }
    
    /**
     *
     * @param filePath
     * @param tagName
     * @return
     */
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
    
    /**
     *
     * @param xml: xml file path
     * @return
     * @throws Exception
     */
    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new FileInputStream(xml));
        return builder.parse(is);
    }
    
    /**
     * Return the folder path of a file
     * Note: suppose the file path is separated by the File.separator
     * 
     * @param filePath
     * @return String : folder path
     */
    public static String getFileContainerPath(String filePath){
        String fileContainerPath = null;
        try{
            String[] filePathInfo = filePath.split("/");
            if(filePathInfo.length == 1){
                return File.separator;
            }
            else{
                filePathInfo[filePathInfo.length-1] = "";
                fileContainerPath = String.join(File.separator, filePathInfo);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return fileContainerPath;
    }
    
    
    /**
     *
     * @param filePath : String
     */
    public static void deleteDirectory(String filePath){
        
        File directory = new File(filePath);
 
    	//make sure directory exists
    	if(!directory.exists()){
 
           System.out.println("Directory does not exist.");
           System.exit(0);
 
        }else{
 
           try{
        	   
               delete(directory);
        	
           }catch(IOException e){
               e.printStackTrace();
               System.exit(0);
           }
        }
 
    	System.out.println("Done");
    }
 
    public static void delete(File file)
    	throws IOException{
 
    	if(file.isDirectory()){
 
    		//directory is empty, then delete it
    		if(file.list().length==0){
    			
    		   file.delete();
    		   System.out.println("Directory is deleted : " 
                                                 + file.getAbsolutePath());
    			
    		}else{
    			
    		   //list all the directory contents
        	   String files[] = file.list();
     
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);
        		 
        	      //recursive delete
        	     delete(fileDelete);
        	   }
        		
        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
           	     file.delete();
        	     System.out.println("Directory is deleted : " 
                                                  + file.getAbsolutePath());
        	   }
    		}
    		
    	}else{
    		//if file, then delete it
    		file.delete();
    		System.out.println("File is deleted : " + file.getAbsolutePath());
    	}
    }
}
