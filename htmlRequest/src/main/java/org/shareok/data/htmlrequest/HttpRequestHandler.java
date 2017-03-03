/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.htmlrequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.shareok.data.htmlrequest.exceptions.ErrorConnectionOutputStreamException;
import org.shareok.data.htmlrequest.exceptions.ErrorHandlingResponseException;
import org.shareok.data.htmlrequest.exceptions.ErrorOpenConnectionException;
import org.shareok.data.htmlrequest.exceptions.ErrorResponseCodeException;
import org.shareok.data.htmlrequest.exceptions.ReadResponseInputStreamException;
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
    
    public void getPdfWithJsoupByUrl(String urlString, String filePath) {
        
        try{
            Connection.Response response = Jsoup.connect(urlString)
                    .ignoreContentType(true)
                    .data("query", "Java")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .cookie("auth", "token")
                    .timeout(300000)
                    .execute();
            
            FileUtils.writeByteArrayToFile(new File(filePath), response.bodyAsBytes());
        }
        catch(Exception e){
            logger.error("Cannot download PDF file from "+urlString, e);
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
           
        BufferedReader in = null;
        HttpURLConnection con = null;
        DataOutputStream wr = null;
        
        try{
            URL obj = new URL(url);
            
            try{
                con = (HttpURLConnection) obj.openConnection();
            }
            catch(Exception e){
                try{
                    con = (HttpsURLConnection) obj.openConnection();
                }
                catch(IOException ex){
                    throw new ErrorOpenConnectionException("Cannot build the connection for file uploading: " + ex.getMessage());
                }
            }

            //add reuqest header
            try{
                con.setRequestMethod("POST");
            }
            catch(ProtocolException ex){
                throw ex;
            }
            
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            if(null != headerInfo && null != headerInfo.entrySet()){
                Iterator it = headerInfo.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry<String, String>)it.next();
                    con.setRequestProperty((String)pair.getKey(), (String)pair.getValue());
                    it.remove();
                }
            }

            // Send request
            con.setDoOutput(true);
            
            if(null != data){
                try{
                    wr = new DataOutputStream(con.getOutputStream());
                    wr.write(data.getBytes("UTF-8"));
                    wr.flush();
                }
                catch(IOException ex){
                    throw new ErrorConnectionOutputStreamException("Cannot get or write into connection output stream: "+ex.getMessage());
                }
            }
            
            Thread.sleep(1000L);

            int responseCode = con.getResponseCode();
            
            if(responseCode != 200){
                throw new ErrorResponseCodeException("The response code = "+responseCode);
            }

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            
            if(null == in){
                throw new ErrorHandlingResponseException("Empty connectin input stream!");
            }
            
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            response.append("code:"+responseCode+"\n");

            try{
                while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                }       
            }
            catch(IOException ex){
                throw new ReadResponseInputStreamException("Cannot read the response input stream: "+ex.getMessage());
            }
            
            return response;
        }
        catch(Exception ex){
            ex.printStackTrace();
            logger.error("Cannot get response with this request!", ex);
        }
        finally{
            try{
                if(in != null){
                    in.close();
                }
                if(con != null){
                    con.disconnect();
                }
                if(wr != null){
                    wr.close();
                }
            }
            catch(IOException ex){
                logger.error("Cannot close the data streams after completing the request", ex);
            }
        }
        
        return null;

    }
    
    public StringBuffer uploadFileByHttpClient(String url, String filePath, Map<String, String>headerInfo) throws ErrorOpenConnectionException, ErrorConnectionOutputStreamException, ErrorResponseCodeException, ErrorHandlingResponseException, ReadResponseInputStreamException{
        
        HttpURLConnection con = null;
        BufferedReader in = null;
        FileInputStream fis = null;
        OutputStream os = null;
        
        try{                
            URL obj = new URL(url);
            
            try{
                con = (HttpURLConnection) obj.openConnection();
            }
            catch(Exception e){
                try{
                    con = (HttpsURLConnection) obj.openConnection();
                }
                catch(IOException ex){
                    throw new ErrorOpenConnectionException("Cannot build the connection for file uploading: " + ex.getMessage());
                }
            }
            
            File file = new File(filePath);
            fis = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(fis);

            try{
                con.setRequestMethod("POST");
            }
            catch(ProtocolException ex){
                throw ex;
            }
            
            if(null != headerInfo && null != headerInfo.entrySet()){
                Iterator it = headerInfo.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry<String, String>)it.next();
                    con.setRequestProperty((String)pair.getKey(), (String)pair.getValue());
                    it.remove();
                }
            }
            
            con.setDoOutput(true);
            
            try{
                os = con.getOutputStream();
                os.write(bytes);
            }
            catch(IOException ex){
                throw new ErrorConnectionOutputStreamException("Cannot get or write into connection output stream: "+ex.getMessage());
            }
            
            Thread.sleep(3000L);
            
            int responseCode = con.getResponseCode();
            
            if(responseCode != 200){
                throw new ErrorResponseCodeException("The response code = "+responseCode);
            }
            
            StringBuffer response = null;

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            if(null == in){
                throw new ErrorHandlingResponseException("Empty connectin input stream!");
            }

            String inputLine;
            response = new StringBuffer();

            response.append("code:").append(responseCode).append("\n");

            try{
                while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                }       
            }
            catch(IOException ex){
                throw new ReadResponseInputStreamException("Cannot read the response input stream: "+ex.getMessage());
            }
            
            return response;
  
        }
        catch(IOException | InterruptedException ex){
            ex.printStackTrace();
            logger.error("Cannot upload file to the location "+url+"!", ex);
        }
        finally{
            try{
                if(null != fis){
                    fis.close();
                }
                if(null != os){
                    os.close();
                }
                if(null != in){
                    in.close();
                }
                if(null != con){
                    con.disconnect();
                }
            }
            catch(IOException ex){
                logger.error("Cannot close file streams after uploading file to the location "+url+"!", ex);
            }
        }
        return null;
    }
}
