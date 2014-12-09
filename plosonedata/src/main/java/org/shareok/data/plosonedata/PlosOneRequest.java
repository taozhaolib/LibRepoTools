/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosonedata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
public class PlosOneRequest {
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    private PlosOneData data;
    
    /**
     * 
     * @param doiInfo: string contain the article doi information, such as 10.1371/journal.pone.0041479
     * @return 
     */
    public String getMetaDataByApi(String doiInfo) {
       
       if(null == doiInfo || "".equals(doiInfo)){
           return "";
       }
       
       String data = null;
       String link = PlosOneUtil.API_SEARCH_PREFIX + doiInfo + "&&api_key=" + PlosOneUtil.API_KEY;
        try {
            StringBuffer temp = sendPost(link);
            data = temp.toString();
        } catch (Exception ex) {
            Logger.getLogger(PlosOneRequest.class.getName()).log(Level.SEVERE, null, ex);
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

    // HTTP POST request
    public StringBuffer sendPost(String url) throws Exception {

            //String url = "http://www.plosbiology.org/article/info:doi/10.1371/journal.pbio.0000012";
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

//            File file = new File("/Users/zhao0677/Projects/plusOne/plosOne.xml");
//            if(!file.exists()){
//                file.createNewFile();
//            }
//            FileWriter writer = new FileWriter(file.getAbsoluteFile());
//            BufferedWriter bw = new BufferedWriter(writer);
//            bw.write(response.toString());
//            bw.close();
            //print result
            //System.out.println(response.toString());

    }

    public String getUSER_AGENT() {
        return USER_AGENT;
    }

    public PlosOneData getData() {
        return data;
    }

    public void setData(PlosOneData data) {
        this.data = data;
    }
    
    
}
