/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.datahandlers.DataHandlersUtil;
import org.shareok.data.datahandlers.exceptions.InvalidDoiException;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.dspacemanager.exceptions.ErrorDspaceApiResponseException;
import org.shareok.data.kernel.api.exceptions.InvalidCommandLineArgumentsException;
import org.shareok.data.kernel.api.exceptions.NoServiceProcessDoiException;
import org.shareok.data.kernel.api.exceptions.NotFoundServiceBeanException;
import org.shareok.data.kernel.api.services.dspace.DspaceJournalDataService;
import org.shareok.data.kernel.api.services.dspace.DspaceRestServiceImpl;
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
        return generateDspaceSafPackagesByDois(dois, null, null);
    }
    
    public static String generateDspaceSafPackagesByDois(String[] dois, String startDate, String endDate){
        String safDownloadPaths = null;
        Map<String, List<String>>doisMap = new HashMap<>();
        for(String doi : dois){
            try{
                String[] doiInfo = DataHandlersUtil.getItemInfoByDoi(doi).split("\\n");
                if(null == doiInfo || doiInfo.length != 2){
                    throw new InvalidDoiException("Cannot get the correct response from crossref for DOI: "+doi);
                }
                else if(!doiInfo[0].equals("200")){
                    throw new InvalidDoiException("The response code is "+ doiInfo[0] + " from crossref for DOI: "+doi);
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
            } catch (InvalidDoiException ex) {
                String error = "error: cannot get correct response from crossref by doi="+doi;
                logger.error(error, ex);
                return error;
            }
        }
        List<String> pathList = new ArrayList<>();
        if(!doisMap.isEmpty()){
            Date now = new Date();            
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
        }
        
        // Now reset the SAF package file name based on start date and end date:
        if(null != startDate && null != endDate){    
            for(int i = 0; i < pathList.size(); i++){
                String safPath = pathList.get(i);
                File saf = new File(safPath);
                String safFilePath = saf.getAbsolutePath();
                String safFileName = saf.getName();
                String safFileNameBase = DocumentProcessorUtil.getFileNameWithoutExtension(safFileName);
                String safFileNameExtension = DocumentProcessorUtil.getFileExtension(safFileName);
                String safFileContainer = DocumentProcessorUtil.getFileContainerPath(safFilePath);
                safFileNameBase += "_" + startDate + "_" + endDate;
                String newFileName = safFileContainer+safFileNameBase+"."+safFileNameExtension;
                try{
                    boolean rename = DocumentProcessorUtil.renameFile(safFilePath, newFileName);
                    if(rename){
                    pathList.set(i, newFileName);
                    }
                    else{
                        pathList.set(i, safFileName+" cannot be renamed to "+newFileName);
                    }
                }
                catch(IOException ex){
                    pathList.set(i, safFileName+" cannot be renamed to "+newFileName+": "+ex.getMessage());
                }
            }
            
        }
        
        safDownloadPaths = DataUtil.getJsonFromStringList(pathList);
        
        return safDownloadPaths;
    }
    
    public static String executeCommandLineTask(String taskId, String data) throws InvalidCommandLineArgumentsException, IOException{
        
        BufferedWriter userInputFileInfoFileWr = null;
        BufferedWriter onputFileInfoFileWr = null;
        String message;
        String taskType = "";
        String startDate;
        String endDate;
        String outputFilePath = null;
        Integer compare;
        JSONObject argumentsObj;
        
        try{
            userInputFileInfoFileWr = new BufferedWriter(new FileWriter(new File(DataHandlersUtil.getUserInputInfoFilePath(taskId, taskType))));            
            if(DocumentProcessorUtil.isEmptyString(data)){
                message = "The data argument is null or empty string: data="+data;                
                userInputFileInfoFileWr.write(message);
                throw new InvalidCommandLineArgumentsException(message);
            }

            JSONObject dataObj = new JSONObject(data);
            for(String key : dataObj.keySet()){
                taskType = key;
                DataHandlersUtil.CURRENT_TASK_TYPE = taskType;
            }
            if(DocumentProcessorUtil.isEmptyString(taskType)){
                message = "The data argument does not specify task type!";
                userInputFileInfoFileWr.write(message);
                throw new InvalidCommandLineArgumentsException(message);
            }
            
            onputFileInfoFileWr = DataHandlersUtil.getWriterForTaskOutputFile(taskId, taskType);

            switch(taskType){
                case "journal-search":                
                    argumentsObj = dataObj.getJSONObject(taskType);
                    String publisher = argumentsObj.getString("publisher");
                    DataHandlersUtil.CURRENT_TASK_ID = taskId;
                    startDate = argumentsObj.getString("startDate");
                    endDate = argumentsObj.getString("endDate");
                    String affiliate = argumentsObj.getString("affiliate");
                    if(DocumentProcessorUtil.isEmptyString(publisher) || DocumentProcessorUtil.isEmptyString(taskId) || DocumentProcessorUtil.isEmptyString(startDate) || 
                        DocumentProcessorUtil.isEmptyString(endDate) || DocumentProcessorUtil.isEmptyString(affiliate)){
                        message = "Cannot get specific items from command line data argument";
                        throw new InvalidCommandLineArgumentsException(message);
                    }
                    compare = DataHandlersUtil.datesCompare(startDate, endDate);
                    if(compare == null){
                        message = "Cannot parse the start date or the end date!";
                        userInputFileInfoFileWr.write(message);
                        throw new InvalidCommandLineArgumentsException(message);
                    }
                    else if(compare > 0){
                        message = "The start date is later than the end date!";
                        userInputFileInfoFileWr.write(message);
                        throw new InvalidCommandLineArgumentsException(message);
                    }
                    try{                    
                        DspaceJournalDataService serviceObj = ServiceUtil.getDspaceJournalDataServInstanceByPublisher(publisher);
                        if(null == serviceObj){
                            onputFileInfoFileWr.write("Cannot get the service bean from task type: "+taskType);
                            return null;
                        }
                        String articlesData = serviceObj.getApiResponseByDatesAffiliate(startDate, endDate, affiliate);                    
                        if(!DocumentProcessorUtil.isEmptyString(articlesData)){
                            articlesData = articlesData.replace("’", "'");
                            outputFilePath = DataHandlersUtil.getTaskFileFolderPath(taskId, taskType) + File.separator + startDate + "_" + endDate + ".json";
                            File outputFile = new File(outputFilePath);
                            if(!outputFile.exists()){
                                outputFile.createNewFile();
                            }
                            DocumentProcessorUtil.outputStringToFile(articlesData, outputFilePath);
                            System.out.println("article data = "+articlesData);
                        }
                        else{
                            System.out.println("The "+taskType+" task id="+taskId+" cannot retrieve the article data!");
                        }
                    }
                    catch(Exception ex){
                        logger.error("Cannot complete the "+taskType+" with id="+taskId, ex);
                    }                
    //                articlesData = articlesData.replaceAll("'", "\\\\\\'");                
                    break;
                case "journal-saf":         
                    String[] dois;
                    try {
                        argumentsObj = dataObj.getJSONObject(taskType);
                        taskId = argumentsObj.getString("id");
                        DataHandlersUtil.CURRENT_TASK_ID = taskId;
                        startDate = argumentsObj.getString("startDate");
                        endDate = argumentsObj.getString("endDate");
                        dois = argumentsObj.getString("dois").split(";");      
                        if(DocumentProcessorUtil.isEmptyString(startDate) || DocumentProcessorUtil.isEmptyString(endDate) || null == dois || dois.length == 0){
                            throw new InvalidCommandLineArgumentsException("Cannot get specific items from command line data argument");
                        }

                        compare = DataHandlersUtil.datesCompare(startDate, endDate);
                        if(compare == null){
                            throw new InvalidCommandLineArgumentsException("Cannot parse the start date or the end date!");
                        }
                        else if(compare > 0){
                            throw new InvalidCommandLineArgumentsException("The start date is later than the end date!");
                        }

                    } catch (JSONException | InvalidCommandLineArgumentsException ex) {
                        logger.error(ex);
                        throw new InvalidCommandLineArgumentsException("Cannot get specific items from command line data argument");
                    }     
                    try{                    
                        outputFilePath = ServiceUtil.generateDspaceSafPackagesByDois(dois, startDate, endDate);
                        if(outputFilePath.startsWith("error")){
                            System.out.println("error message received: "+outputFilePath);
                            throw new ErrorDspaceApiResponseException("Cannot get saf path because error message received: "+outputFilePath);
                        }
                        else if(null == outputFilePath){
                            throw new ErrorDspaceApiResponseException("Cannot get null saf path!");
                        }
                        else{
                            System.out.println("The SAF package has been stored at path="+outputFilePath);
                        }
                    }
                    catch(Exception ex){
                        logger.error(ex);
                        ex.printStackTrace();
                    }
                    break;
                case "journal-import":
                    try{
                        argumentsObj = dataObj.getJSONObject(taskType);
                        taskId = argumentsObj.getString("id");
                        DataHandlersUtil.CURRENT_TASK_ID = taskId;
                        outputFilePath = ShareokdataManager.getDspaceCommandLineTaskOutputPath() + "_" + DataHandlersUtil.getCurrentTimeString()+"_"+taskType+"_"+taskId+".txt";
                        String safPath = argumentsObj.getString("safPath");
                        String collectionHandle = argumentsObj.getString("collectionHandle");
                        String dspaceApiUrl = argumentsObj.getString("dspaceApiUrl");
                        DspaceRestServiceImpl ds = (DspaceRestServiceImpl)getDataService("rest-import-dspace");
                        ds.getHandler().setReportFilePath(DataHandlersUtil.getJobReportPath("cli-import-dspace-"+taskType, taskId)+ File.separator + taskId + "-report.txt");
                        ds.loadItemsFromSafPackage(safPath, collectionHandle, dspaceApiUrl);                    
                    }
                    catch(Exception ex){
                        logger.error(ex);
                        ex.printStackTrace();
                    }
                    break;
                default:
                    throw new InvalidCommandLineArgumentsException("The command line task type is valid!");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            if(null != userInputFileInfoFileWr){
                try{
                    userInputFileInfoFileWr.flush();
                    userInputFileInfoFileWr.close();                    
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
            
            if(null != onputFileInfoFileWr){
                try{
                    onputFileInfoFileWr.flush();
                    onputFileInfoFileWr.close();                    
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }
        return outputFilePath;
    }

}
