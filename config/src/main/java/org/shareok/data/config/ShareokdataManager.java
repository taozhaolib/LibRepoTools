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
import java.util.Date;
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
        String shareokdataPath = prop.getProperty("shareokdataPath");
        File shareokdataPathFile = new File(shareokdataPath);
        if(!shareokdataPathFile.exists()){
            shareokdataPathFile.mkdir();
        }
        return shareokdataPath;
    }
    
    public static String getSageUploadPath(){
        if(null == prop){
            loadProperties();
        }
        String sageUploadPath = prop.getProperty("sageUploadPath");
        File sageUploadPathFile = new File(sageUploadPath);
        if(!sageUploadPathFile.exists()){
            sageUploadPathFile.mkdir();
        }
        return sageUploadPath;
    }
    
    public static String getOuhistoryUploadPath(){
        if(null == prop){
            loadProperties();
        }
        String sageUploadPath = prop.getProperty("ouhistoryUploadPath");
        File sageUploadPathFile = new File(sageUploadPath);
        if(!sageUploadPathFile.exists()){
            sageUploadPathFile.mkdir();
        }
        return sageUploadPath;
    }
    
    public static String getPlosUploadPath(){
        if(null == prop){
            loadProperties();
        }
        String plosUploadPath = prop.getProperty("plosUploadPath");
        File plosUploadPathFile = new File(plosUploadPath);
        if(!plosUploadPathFile.exists()){
            plosUploadPathFile.mkdir();
        }
        return plosUploadPath;
    }
    
    public static String getSafUploadPath(){
        if(null == prop){
            loadProperties();
        }
        String safUploadPath = prop.getProperty("shareokdataPath");
        File safUploadPathFile = new File(safUploadPath);
        if(!safUploadPathFile.exists()){
            safUploadPathFile.mkdir();
        }
        return safUploadPath;
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
    
    public static String getSafPackageDataServiceBean(String publisher){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("safPackageDataBean."+publisher);
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
    
    public static String getRedisJobUserIdMatchingTable(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisJobUserIdMatchingTable");
    }
    
    public static String getRedisServerNameIdMatchingTable(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisServerNameIdMatchingTable");
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
       
    public static String getRedisServerQueryPrefix(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("redisServerQueryPrefix");
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
    
    public static int getRedisJobQueueMaxJobs(){
        if(null == prop){
            loadProperties();
        }
        return Integer.valueOf(prop.getProperty("redisJobQueueMaxJobs"));
    }
    
    public static String getDspaceRestImportPath(String jobType){
        String filePath = "";
        try{
            String shareokdataPath = getShareokdataPath();
            String repoType = jobType.split("-")[2];
            filePath = shareokdataPath + File.separator + repoType;
            File file = new File(filePath);
            if(!file.exists()){
                file.mkdir();
            }
            filePath += File.separator + jobType;
            file = new File(filePath);
            if(!file.exists()){
                file.mkdir();
            }
            filePath += File.separator + ShareokdataManager.getSimpleDateFormat().format(new Date()).replace(" ", "_").replace(":", "-");
            file = new File(filePath);
            if(!file.exists()){
                file.mkdir();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return filePath;
    }
    
    public static String getAwsS3BucketUriPrefix(){
        if(null == prop){
            loadProperties();
        }
        return prop.getProperty("s3BucketUriPrefix");
    }
}
