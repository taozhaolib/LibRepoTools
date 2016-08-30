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
import java.util.Date;
import org.shareok.data.config.DataUtil;
import java.util.HashMap;
import java.util.Map;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.services.job.RedisJobService;
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
    
    private static final Map<String, String> serviceBeanMap = new HashMap<>();
    static {
        for(String job : DataUtil.JOB_TYPES){
            switch(job){
                case "ssh-import-dspace":
                case "ssh-upload-dspace":
                case "ssh-importloaded-dspace":
                    serviceBeanMap.put(job, "dspaceSshServiceImpl");
                    break;
                case "ssh-import-islandora":
                    serviceBeanMap.put(job, "islandoraSshServiceImpl");
                    break;
                case "rest-import-dspace":
                    serviceBeanMap.put(job, "dspaceRestServiceImpl");
                    break;
                default:
                    break; 
            }
        }
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
    
    public static DataService getDataService(ApplicationContext context, String jobType){
        String bean = serviceBeanMap.get(jobType);  
        if(null == context){
            context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        }
        DataService ds = (DataService) context.getBean(bean);
        return ds;
    }
    
    public static DataService getDataService(ApplicationContext context, int jobType){
        return getDataService(context, DataUtil.JOB_TYPES[jobType]);
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
    
    public static String getRepoTypeByJob(RedisJob job){
        String jobType = DataUtil.JOB_TYPES[job.getType()];
        if(null != jobType && !"unknown".equals(jobType)){
            String repoTypeStr = jobType.split("-")[2];
            if(null != repoTypeStr){
                if(repoTypeStr.equals("dspace")){
                    return "DSpace";
                }
                if(repoTypeStr.equals("islandora")){
                    return "Islandora";
                }
                if(repoTypeStr.equals("hydra")){
                    return "Hydra";
                }
            }
        }
        return "";
    }
    
    public static Thread getThreadByName(String name){
        for(Thread th : Thread.getAllStackTraces().keySet()){
            String threadName = th.getName();
            if(null != threadName && threadName.equals(name)){
                return th;
            }
        }
        return null;
    }
    
    public static RedisJobService getJobService(){
        ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        return (RedisJobService) context.getBean("redisJobServiceImpl");
    }
    
    public static void processJobReturnValue(String jobReturnValue, RedisJob job){
        RedisJobService redisJobServ = ServiceUtil.getJobService();
        long jobId = job.getJobId();
        int jobType = job.getType();
        if(null != jobReturnValue && !jobReturnValue.equals("")){
                redisJobServ.updateJob(jobId, "status", "2");  
                Map values = new HashMap();
                switch(jobType){
                    case 1:
                    case 3:
                        values.put("uploadedPackagePath", jobReturnValue);
                        redisJobServ.updateJobInfoByJobType(jobId, DataUtil.JOB_TYPES[jobType], values);
                        if(jobType == 3){
                            redisJobServ.updateJob(jobId, "uploadedPackagePath", jobReturnValue);
                            redisJobServ.updateJob(jobId, "status", "6");
                        }
                        else if(jobType == 1){
                            redisJobServ.updateJob(jobId, "status", "2");
                        }
                        break;
                    case 4:
                    case 5:
                        redisJobServ.updateJob(jobId, "status", "2");
                        break;
                    default:
                        break;
                }
            }
            else{
                redisJobServ.updateJob(jobId, "status", "3");
            }
        redisJobServ.updateJob(jobId, "endTime", ShareokdataManager.getSimpleDateFormat().format(new Date()));
    }
}
