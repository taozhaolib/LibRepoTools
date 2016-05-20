/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tao Zhao
 */
public class ShareokdataManager {
    
    private static Properties prop;
    
    public static void setProperties(Properties properties){
        prop = properties;
    }
    
    public static void loadProperties(){
        Properties properties = new Properties();
        InputStream input = null;
        try{
            input = ShareokdataManager.class.getClassLoader().getResourceAsStream("shareokdata.properties");
            properties.load(input);
        }
        catch(IOException ioex){
            Logger.getLogger(ShareokdataManager.class.getName()).log(Level.SEVERE, null, ioex);
        }
        finally{
            try{
                if(null != input){
                    input.close();
                }
            }
            catch(IOException ioex){
                Logger.getLogger(ShareokdataManager.class.getName()).log(Level.SEVERE, null, ioex);
            }
        }
        prop = properties;
    }
    
    public static String getShareokdataPath(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("shareokdataPath");
    }
    
    public static String getSageUploadPath(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("sageUploadPath");
    }
    
    public static String getPlosUploadPath(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("plosUploadPath");
    }
    
    public static String getSafUploadPath(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("safUploadPath");
    }
    
    public static String getUploadPathFunction(String publisher){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("uploadPathFunction."+publisher);
    }
    
    public static String getJournalDataServiceBean(String publisher){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("journalDataBean."+publisher);
    }
    
    public static String getDspceSampleDublinCoreFileName(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("dspceSampleDublinCoreFileName");
    }
    
    public static String getRedisGlobalUidSchema(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisGlobalUid"); 
    }
    
    public static String getRedisGlobalServerIdSchema(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisGlobalServerId"); 
    }
    
    public static String getRedisUserIdQueryPrefix(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisUserIdQueryPrefix");
    }
    
    public static String getRedisUserNameIdMatchingTable(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisUserNameIdMatchingTable");
    }
    
    public static boolean requiredUserAuthentication(String urlPattern){
        boolean userAuthenRequired = true;
        if(null == prop){
            loadProperties();
        }
        String[] noAuthenPatterns = prop.getProperty("noUserAuthen").replaceAll("\\s*#\\s*","#").split(",");
        userAuthenRequired = !Arrays.asList(noAuthenPatterns).contains(urlPattern);
        return userAuthenRequired;
    }
    
    public static String getSessionRedisUserAttributeName(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("sessionUserAttributeName");
    }
    
    public static String getReportSshDspaceImport(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("reportSshDspaceImport");
    }
    
    public static String getRedisGlobalJobIdSchema(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisGlobalJobId"); 
    }
    
    public static String getRedisJobQueryPrefix(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisJobIdQueryPrefix");
    }
    
    public static String getRedisServerQueryPrefix(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisServerIdQueryPrefix");
    }
    
    public static String getRedisServerNameIdMatchingTable(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisServerNameIdMatchingTable");
    }
    
    public static String getRedisJobUserIdMatchingTable(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisJobUserIdMatchingTable");
    }
    
    public static int getRedisJobTypeImport(){
        if(null == prop){
            loadProperties();
        }
        return Integer.valueOf(prop.getProperty("redisJobTypeImport"));
    }
    
    public static int getRedisJobTypeExport(){
        if(null == prop){
            loadProperties();
        }
        return Integer.valueOf(prop.getProperty("redisJobTypeExport"));
    }
    
    public static String getDateFormat(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("dateFormat");
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(ShareokdataManager.getDateFormat());
    }
    
    public static String getJobReportPath(String repoType, String jobType, long jobId){
        String shareokdataPath = getShareokdataPath();
        String filePath = shareokdataPath + File.separator + repoType;
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        filePath += File.separator + jobType;
        file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        filePath += File.separator + String.valueOf(jobId);
        file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        return filePath;
    }
}
