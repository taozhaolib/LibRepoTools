/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.datahandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.util.FileUtil;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import static org.shareok.data.config.ShareokdataManager.getShareokdataPath;
import org.shareok.data.datahandlers.exceptions.SecurityFileDoesNotExistException;
import org.shareok.data.htmlrequest.HttpRequestHandler;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class DataHandlersUtil {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DataHandlersUtil.class);
    
    public static String getJobReportPath(String jobType, long jobId){
        String shareokdataPath = getShareokdataPath();
        String repoType = jobType.split("-")[2];
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
    
    public static String getJobReportFilePath(String jobType, long jobId){
        return getJobReportPath(jobType, jobId) + File.separator + String.valueOf(jobId) + "-report.txt";
    }
    
    public static String getJobReportFilePath(RedisJob job){
        return getJobReportPath(DataUtil.JOB_TYPES[job.getType()], job.getJobId()) + File.separator + String.valueOf(job.getJobId()) + "-report.txt";
    }
    
    public static String[] getRepoCredentials(String repoName) throws SecurityFileDoesNotExistException, IOException{
        String[] credentials = new String[2];
        String securityFilePath = ShareokdataManager.getSecurityFilePath();
        File securityFile = new File(securityFilePath);
        if(!securityFile.exists()){
            throw new SecurityFileDoesNotExistException("The security file does NOT exist!");
        }
        String content = new String(Files.readAllBytes(Paths.get(securityFilePath)));
        ObjectMapper mapper = new ObjectMapper();
        RepoCredential[] credentialObjects = mapper.readValue(content, RepoCredential[].class);
        for(RepoCredential credentialObj : credentialObjects){
            if(repoName.equals(credentialObj.getRepoName())){
                credentials[0] = credentialObj.getUserName();
                credentials[1] = credentialObj.getPassword();
            }
        }
        return credentials;
    }
    
    public static String getDomainNameFromUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
    
    public static String getItemInfoByDoi(String doi){
        String response = null;
        ApplicationContext context = new ClassPathXmlApplicationContext("htmlRequestContext.xml");
        HttpRequestHandler handler = (HttpRequestHandler) context.getBean("httpRequestHandler");
        String url = "http://api.crossref.org/works/" + doi;
        try{
            response = handler.sendGet(url);
        }
        catch(Exception ex){
            logger.error("Cannot get item information by Doi", ex);
        }
        return response;
    }
    
    /**
     * Get the key from file name to retrieve the download path from database
     * 
     * @param fileName
     * @return : key
     */
    public static String getFileNameKeyForDownloadPath(String fileName){
        return FilenameUtils.removeExtension(fileName);
    }
}
