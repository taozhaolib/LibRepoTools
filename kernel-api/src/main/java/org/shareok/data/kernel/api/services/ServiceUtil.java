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
import java.util.ArrayList;
import java.util.Date;
import org.shareok.data.config.DataUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.datahandlers.DataHandlersUtil;
import org.shareok.data.datahandlers.exceptions.InvalidDoiException;
import org.shareok.data.kernel.api.exceptions.IncorrectDoiResponseException;
import org.shareok.data.kernel.api.exceptions.NoServiceProcessDoiException;
import org.shareok.data.kernel.api.exceptions.NotFoundServiceBeanException;
import org.shareok.data.kernel.api.services.dspace.DspaceJournalDataService;
import org.shareok.data.kernel.api.services.job.DspaceApiJobServiceImpl;
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
    
    public static final Map<String, String> serviceBeanMap = new HashMap<>();
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
        serviceBeanMap.put("Public Library of Science (PLoS)", "plosDspaceServiceImpl");
        serviceBeanMap.put("plos", "plosDspaceServiceImpl");
        serviceBeanMap.put("sage", "sageDspaceServiceImpl");
        serviceBeanMap.put("SAGE Publications", "sageDspaceServiceImpl");
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
    
    public static DataService getDataService(String jobType){
        String bean = serviceBeanMap.get(jobType);  
        ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        DataService ds = (DataService) context.getBean(bean);
        return ds;
    }
    
    public static DataService getDataService(int jobType){
        return getDataService(DataUtil.JOB_TYPES[jobType]);
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

    public static boolean downloadRemoteFile(String link, String targetFile) {
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
//                redisJobServ.updateJob(jobId, "status", "2");  
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
                    case 2:
                    case 4:
                    case 5:
                    default:
                        redisJobServ.updateJob(jobId, "status", "2");
                        break;
                }
            }
            else{
                redisJobServ.updateJob(jobId, "status", "3");
            }
        redisJobServ.updateJob(jobId, "endTime", ShareokdataManager.getSimpleDateFormat().format(new Date()));
    }
    
    public static String getThreadNameByJob(RedisJob job){
        long uid = job.getUserId();
        String jobType = DataUtil.JOB_TYPES[job.getType()];
        int serverId = job.getServerId();
        long jobId = job.getJobId();
        return String.valueOf(uid)+"--"+jobType+"--"+String.valueOf(serverId)+"--"+String.valueOf(jobId);
    }
    
    public static RedisJobService getJobServiceByJobType(RedisJob job){
        ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        switch(job.getType()){
            case 2:
                return (DspaceApiJobServiceImpl)context.getBean("dspaceApiJobServiceImpl");
            default:
                return (RedisJobService) context.getBean("redisJobServiceImpl");
        }
    }
    
    public static DspaceJournalDataService getDspaceJournalDataServInstanceByPublisher(String publisher){
        DspaceJournalDataService obj = null;
        try {
            String bean = serviceBeanMap.get(publisher);
            if(null == bean){
                throw new NotFoundServiceBeanException("No beans found for publisher "+publisher);
            }
            ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
            return (DspaceJournalDataService)context.getBean(bean);            
        } catch (NotFoundServiceBeanException ex) {
            logger.error("Cannot create the instance to get item information by DOI! ", ex);
        }
        return obj;
    }
    
    public static String generateDspaceSafPackagesByDois(String[] dois){
        String safDownloadPaths = null;
        Map<String, List<String>>doisMap = new HashMap<>();
        for(String doi : dois){
            try{
                String[] doiInfo = DataHandlersUtil.getItemInfoByDoi(doi).split("\\n");
                if(null == doiInfo || doiInfo.length != 2){
                    throw new IncorrectDoiResponseException("Cannot get the correct response from crossref for DOI: "+doi);
                }
                else if(!doiInfo[0].equals("200")){
                    throw new IncorrectDoiResponseException("The response code is "+ doiInfo[0] + " from crossref for DOI: "+doi);
                }
                else{
                    JSONObject obj = new JSONObject(doiInfo[1]);
                    String url = obj.getJSONObject("message").getString("URL");
                    String publisher = obj.getJSONObject("message").getString("publisher");
                    String key = ServiceUtil.serviceBeanMap.get(publisher);
                    List list = doisMap.get(key);
                    if(null == list){
                        list = new ArrayList<>();
                    }
                    list.add(doi);
                    doisMap.put(key, list);                                
                }
            }
            catch(IncorrectDoiResponseException ex){
                logger.error("Cannot get correct response from crossref by doi", ex);
            } catch (InvalidDoiException ex) {
                logger.error(ex);
            }
        }
        if(!doisMap.isEmpty()){
            Date now = new Date();
            List<String> pathList = new ArrayList<>();
            ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");        
            for(String key : doisMap.keySet()){
                try{
                    DspaceJournalDataService serviceObj = (DspaceJournalDataService)context.getBean(key);
                    if(null == serviceObj){
                        throw new NoServiceProcessDoiException("Cannot find the DOI processing service for bean " + key + "!");
                    }
                    String[] doisByPubliser = doisMap.get(key).toArray(new String[doisMap.get(key).size()]);
                    String safFilePath = serviceObj.getDspaceJournalLoadingFilesByDoi(doisByPubliser, now);
                    pathList.add(safFilePath);
                }
                catch(Exception ex){
                    logger.error("Cannot get DspaceJournalDataService object to generate SAF package with bean = "+key, ex);
                }
            }    
            safDownloadPaths = DataUtil.getJsonFromStringList(pathList);
        }
        return safDownloadPaths;
    }
}
