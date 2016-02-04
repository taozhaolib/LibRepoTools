
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tao Zhao
 */
public class ShareokdataConfig {
    
    private static Properties prop;
    
    public static void setProperties(Properties properties){
        prop = properties;
    }
    
    public static Properties loadProperties(){
        Properties prop = new Properties();
        try{
            InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("shareokdata.properties");
            prop.load(input);
        }
        catch(IOException ioex){
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ioex);
        }
        return prop;
    }
    
    public static String getSageUploadPath(){
        if(null == prop){
            Properties prop = loadProperties();
        }
        return prop.getProperty("sageUploadPath");
    }
    
    public static String getPlosUploadPath(){
        if(null == prop){
            Properties prop = loadProperties();
        }
        return prop.getProperty("plosUploadPath");
    }
}
