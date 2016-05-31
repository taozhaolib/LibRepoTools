/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.shareok.data.config.DataUtil;
import java.util.HashMap;
import java.util.Map;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class ServiceUtil {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceUtil.class);
    
    private static final Map<String, String> serviceBeanMap = new HashMap<String, String>();
    static {
        serviceBeanMap.put("dspace-ssh-import", "dspaceSshServiceImpl");
    }
    
    public static String getServiceBean(int repoType, int jobType){
        return serviceBeanMap.get(DataUtil.REPO_TYPES[repoType] + "-" + DataUtil.JOB_TYPES[jobType]);
    }
    
    /**
     * 
     * @param repoType : e.g. "dspace" or "islandora", lower case only
     * @param jobType : e.g. "ssh-import" or "rest-import", lower case only
     * @return bean name
     */
    public static String getServiceBean(String repoType, String jobType){
        return serviceBeanMap.get(repoType + "-" + jobType);
    }
    
    public static DataService getDataService(ApplicationContext context, String repoType, String jobType){
        String bean = serviceBeanMap.get(repoType + "-" + jobType);  
        if(null == context){
            context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        }
        DataService ds = (DataService) context.getBean(bean);
        return ds;
    }
    
    public static DataService getDataService(ApplicationContext context, int repoType, int jobType){
        return getDataService(context, DataUtil.REPO_TYPES[repoType], DataUtil.JOB_TYPES[jobType]);
    }
    
    public static String saveUploadedFile(MultipartFile file, String jobFilePath){
        String uploadedFilePath = null;
        try{
            String fileName = file.getOriginalFilename();
            File path = new File(jobFilePath);
            if(!path.exists()){
                path.mkdir();
            }
            uploadedFilePath = jobFilePath + File.separator + fileName;
            File uploadedFile = new File(uploadedFilePath);
            file.transferTo(uploadedFile);
        }
        catch(IOException | IllegalStateException ex){
            logger.error("Cannot save the uploaded file", ex);
        }
        return uploadedFilePath;
    }

    public static boolean downloadFile(String link, String targetFile) {
        ByteArrayOutputStream out = null;
        InputStream in = null;
        FileOutputStream fos = null;
        try {
            URL linkUrl = new URL(link);
            in = new BufferedInputStream(linkUrl.openStream());
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buffer))) {
                out.write(buffer, 0, n);
            }
            byte[] response = out.toByteArray();
            fos = new FileOutputStream(targetFile);
            fos.write(response);
            return true;
        } catch (MalformedURLException mex) {
            logger.error("Cannot set up the url for file download.", mex);
        } catch (IOException ioex) {
            logger.error("Cannot open the stream for the link URL.", ioex);
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                if (null != in) {
                    in.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException ioex) {
                logger.error("Cannot close the outputstream when downloading file online.", ioex);
            }
        }
        return false;
    }
    
    public static String getReportFilePathByJob(RedisJob job){
        return ShareokdataManager.getShareokdataPath() + File.separator + DataUtil.REPO_TYPES[job.getRepoType()] 
                + File.separator + DataUtil.JOB_TYPES[job.getType()] + File.separator + String.valueOf(job.getJobId()) 
                + File.separator + String.valueOf(job.getJobId()) + "-report.txt";
    }
    
    public static RepoServerService getRepoServerServiceInstance(ApplicationContext context){
        if(null == context){
            context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        }
        return (RepoServerService) context.getBean("repoServerServiceImpl");
    }
}
