/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oulib.aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tao Zhao
 */
public class AwsUtil {
    
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String CREDENTIALS_REQUEST_URL = "http://169.254.169.254/latest/meta-data/iam/security-credentials/lib-amz-default";
    
    public static String getAwsCredentials(){
        
        BufferedReader in = null;
                
        try{
            URL obj = null;
            try {
                obj = new URL(CREDENTIALS_REQUEST_URL);
            } catch (MalformedURLException ex) {
                Logger.getLogger(AwsUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            try {
                // optional default is GET
                con.setRequestMethod("GET");
            } catch (ProtocolException ex) {
                Logger.getLogger(AwsUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + CREDENTIALS_REQUEST_URL);
            System.out.println("Response Code : " + responseCode);

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
            }
            return response.toString();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        finally{
            if(null != in){
                try{
                    in.close();
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
}
