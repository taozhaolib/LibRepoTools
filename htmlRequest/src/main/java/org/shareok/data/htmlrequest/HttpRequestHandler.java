/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.htmlrequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Tao Zhao
 */
public class HttpRequestHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HttpRequestHandler.class);
    
    private final String USER_AGENT = "Mozilla/5.0";
    
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
    public String sendGet(String url) {

        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            response.append(responseCode);
            response.append("\n");

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();

            return response.toString();
        }
        catch (MalformedURLException ex){
            logger.error("The url has bad format!", ex);
        } catch (ProtocolException ex) {
            logger.error("Bad http request method protocol!", ex);
        } catch (IOException ex) {
            logger.error("Cannot get the response code!", ex);
        }
        return null;
    }

    public StringBuffer sendPost(String url) throws Exception {
           
            URL obj = new URL(url);
            HttpURLConnection con;
            
            try{
                con = (HttpURLConnection) obj.openConnection();
            }
            catch(Exception e){
                con = (HttpsURLConnection) obj.openConnection();
            }

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\n\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();
            
            return response;

    }
    
    public void getPdfByUrl(String urlString, String filePath) {

        try{
            URL url = new URL(urlString);
            try (InputStream in = url.openStream(); 
                 FileOutputStream fos = new FileOutputStream(new File(filePath))) {

                int length = -1;
                byte[] buffer = new byte[1024];// buffer for portion of data from
                // connection
                while ((length = in.read(buffer)) > -1) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
            }
            System.out.println("PDF file was downloaded");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param url
     * @param method : POST/GET/DELETE/PUT
     * @param headerInfo: Information to set up the header
     * @param data : data to be sent to the server
     *          
     * @return : the response has two lines: first line is the response code and the second line is the text info
     */
    public StringBuffer requestWithHeaderInfo(String url, String method, Map<String, String>headerInfo, String data) {
           
        try{
            URL obj = new URL(url);
            HttpURLConnection con;
            
            try{
                con = (HttpURLConnection) obj.openConnection();
            }
            catch(Exception e){
                con = (HttpsURLConnection) obj.openConnection();
            }

            //add reuqest header
            con.setRequestMethod(method);
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            Iterator it = headerInfo.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry<String, String>)it.next();
                con.setRequestProperty((String)pair.getKey(), (String)pair.getValue());
                it.remove();
            }

            // Send request
            con.setDoOutput(true);
            
            if(null != data){
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.write(data.getBytes("UTF-8"));
                wr.flush();
                wr.close();
            }
            

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            response.append("code:"+responseCode+"\n");

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            in.close();
            
            return response;
        }
        catch(Exception ex){
            logger.error("Cannot get response with this request!", ex);
        }
        
        return null;

    }
}
