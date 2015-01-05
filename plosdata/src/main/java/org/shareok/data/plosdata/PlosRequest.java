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
import org.shareok.data.htmlrequest.HtmlRequest;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tao Zhao
 */
public class PlosRequest {
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    private HtmlRequest htmlRequest;
    
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
       String link = PlosUtil.API_SEARCH_PREFIX + doiInfo + "&&api_key=" + PlosUtil.API_KEY;
        try {
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
       
       String encodedDoiInfo = doiInfo.replace("/", "%2F");
       String link = PlosUtil.URL_FULLTEXT_PREFIX + encodedDoiInfo;
       
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
    
    public void getOutput(String fileName) throws IOException {
        try {
                File file = new File(fileName);
                if(!file.exists()){
                    file.createNewFile();
                }
            
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("company");
		doc.appendChild(rootElement);
 
		// staff elements
		Element staff = doc.createElement("Staff");
		rootElement.appendChild(staff);
 
		// set attribute to staff element
		Attr attr = doc.createAttribute("id");
		attr.setValue("1");
		staff.setAttributeNode(attr);
 
		// shorten way
		// staff.setAttribute("id", "1");
 
		// firstname elements
		Element firstname = doc.createElement("firstname");
		firstname.appendChild(doc.createTextNode("yong"));
		staff.appendChild(firstname);
 
		// lastname elements
		Element lastname = doc.createElement("lastname");
		lastname.appendChild(doc.createTextNode("mook kim"));
		staff.appendChild(lastname);
 
		// nickname elements
		Element nickname = doc.createElement("nickname");
		nickname.appendChild(doc.createTextNode("mkyong"));
		staff.appendChild(nickname);
 
		// salary elements
		Element salary = doc.createElement("salary");
		salary.appendChild(doc.createTextNode("100000"));
		staff.appendChild(salary);
 
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
    }

 
    // HTTP GET request
    private void sendGet() throws Exception {

            String url = "http://www.google.com/search?q=mkyong";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());

    }
    
    public String getImportedDataPath(String doi) {
        
        String path = "";
        try{
            
            String resource = PlosRequest.class.getName().replace(".", File.separator) + ".class";      
            URL fileURL = ClassLoader.getSystemClassLoader().getResource(resource);
            path = new File(fileURL.toURI()).getParent();
            String folderName = doi.split("/")[1];
            path = path + File.separator + "importedData" + File.separator + folderName;
        }
        catch(URISyntaxException ex){
            ex.printStackTrace();
        }
        return path;
    }

    public void downloadPlosOnePdfByDoi(String doi) {
        
        try{
            boolean directoryExists = false;
            String path = getImportedDataPath(doi);
            File newDir = new File(path);
            if(newDir.exists()){
                directoryExists = true;
                System.out.println("The directory: "+newDir+" has already exists!\n");
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
                String pdfUrl = PlosUtil.API_FULLTEXT_PDF_PREFIX + doi + ".pdf";
                getHtmlRequest().getPdfByUrl(pdfUrl, newDir+"/"+doi.split("/")[1]+".pdf");
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public String getUSER_AGENT() {
        return USER_AGENT;
    }

    public HtmlRequest getHtmlRequest() {
        return htmlRequest;
    }

    public void setHtmlRequest(HtmlRequest htmlRequest) {
        this.htmlRequest = htmlRequest;
    }
    
}
