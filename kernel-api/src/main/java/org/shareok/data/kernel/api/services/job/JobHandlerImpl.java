/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.DataHandler;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.exceptions.EmptyUploadedPackagePathOfSshUploadJobException;
import org.shareok.data.kernel.api.services.DataService;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class JobHandlerImpl implements JobHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JobHandlerImpl.class);
    
    private JobQueueService jobQueueService;

    @Autowired
    public void setJobQueueService(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }
    
    /**
     * 
     * @param uid : user ID
     * @param handler : handler of job execution
     * @param localFile : uploaded file from local computer
     * @param remoteFilePath : remote resource of the file
     * @return : path to the report file
     */
    @Override
    public RedisJob execute(long uid, DataHandler handler, MultipartFile localFile, String remoteFilePath){
        try{
            ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");

            RedisJobService redisJobServ = (RedisJobService) context.getBean("redisJobServiceImpl");

            RedisJob newJob = redisJobServ.createJob(uid, handler.getJobType(), handler.outputJobDataByJobType());
            long jobId = newJob.getJobId();

            String jobFilePath = ShareokdataManager.getJobReportPath(handler.getRepoType(), DataUtil.JOB_TYPES[handler.getJobType()], jobId);

            DataService ds = ServiceUtil.getDataService(context, handler.getJobType());

            String filePath = "";
            String reportFilePath = jobFilePath + File.separator + String.valueOf(jobId) + "-report.txt";

            if(null != localFile && !localFile.isEmpty()){
                filePath = ServiceUtil.saveUploadedFile(localFile, jobFilePath);
            }
            else if(null != remoteFilePath && !"".equals(remoteFilePath)){
                filePath = processRemoteFileByJobType(handler.getJobType(), redisJobServ, jobFilePath, remoteFilePath);
            }

            handler.setFilePath(filePath);
            handler.setReportFilePath(reportFilePath);
            ds.setUserId(uid);
            ds.setHandler(handler);
            
            redisJobServ.updateJob(jobId, "status", "0"); 
            redisJobServ.updateJob(jobId, "filePath", filePath); 
            
            //Handle the job queue stuff:
            String queueName = RedisUtil.getJobQueueName(uid, DataUtil.JOB_TYPES[handler.getJobType()], handler.getServerName());    
            Thread thread = ServiceUtil.getThreadByName(queueName);
            if(jobQueueService.isJobQueueEmpty(queueName)){                
                if(null != thread && !thread.isInterrupted()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        logger.debug("Current thread is interrupted while sleeping", ex);
                    }
                    if(!thread.isInterrupted()){
                        jobQueueService.addJobIntoQueue(jobId, queueName);
                        redisJobServ.updateJob(jobId, "status", "7"); 
                    }
                    else{
//                        try {
//                            thread.join();
//                        } catch (InterruptedException ex) {
//                            logger.debug("Current thread is interrupted while sleeping", ex);
//                        }
                        jobQueueService.addJobIntoQueue(jobId, queueName);
                        redisJobServ.updateJob(jobId, "status", "7"); 
                        Thread newThread = new Thread(ds, queueName);
                        newThread.start();
                    }
                }
                else{
//                    if(null != thread && thread.isInterrupted()){
//                        while(thread.isAlive()){
//                            try {
//                                Thread.sleep(50L);
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(JobHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }
//
//                    }
                    jobQueueService.addJobIntoQueue(jobId, queueName);
                    redisJobServ.updateJob(jobId, "status", "7"); 
                    Thread newThread = new Thread(ds, queueName);
                    newThread.start();
                }
            }
            else{
                jobQueueService.addJobIntoQueue(jobId, queueName);
                redisJobServ.updateJob(jobId, "status", "7"); 
                if(null == thread){
                    Thread newThread = new Thread(ds, queueName);
                    newThread.start();
                }                
            }
         
            return redisJobServ.findJobByJobId(jobId);
        }
        catch(BeansException | NumberFormatException | EmptyUploadedPackagePathOfSshUploadJobException ex){
//            logger.error("Cannot exectue the job with type "+DataUtil.JOB_TYPES[handler.get]+" for repository "+DataUtil.REPO_TYPES[repoType], ex);
            logger.error("Cannot exectue the job with type.", ex);
        }
        return null;
    }
    
//    @Override
//    public RedisJob execute(long uid, String jobType, String repoType, DataHandler handler, MultipartFile localFile, String remoteFilePath){
//        return execute(uid, Arrays.asList(DataUtil.JOB_TYPES).indexOf(jobType), Arrays.asList(DataUtil.REPO_TYPES).indexOf(repoType), handler, localFile, remoteFilePath);
//    }
    
    private String processRemoteFileByJobType(int jobType, RedisJobService redisJobServ, String jobFilePath, String remoteFilePath) throws EmptyUploadedPackagePathOfSshUploadJobException{
        String filePath = "";
        switch(jobType){
            case 5:
                filePath = "uri--" + remoteFilePath;                
            case 4:
                if(remoteFilePath.contains("job-")){
                    long oldJobId = Long.parseLong(remoteFilePath.split("-")[1]);
                    Map values = redisJobServ.getJobInfoByAttributes(oldJobId, new String[]{"uploadedPackagePath", "status"});
                    if(null == values.get("status") || !"6".equals((String)values.get("status"))){
                        return null;
                    }
                    Object obj = values.get("uploadedPackagePath");
                    if(null == obj || "".equals((String)obj)){
                        throw new EmptyUploadedPackagePathOfSshUploadJobException("The ssh-upload job does NOT have a path to the uploaded package in the server.");
                    }
                    filePath = (String)obj;
                }
                else{
                    filePath = remoteFilePath;
                }
                break;
            default:
                filePath = jobFilePath + File.separator + "downloaded.zip";
                ServiceUtil.downloadFile(remoteFilePath, filePath);
                break;
        }
        return filePath;
    }
    
    private void processJobReturnValue(String jobReturnValue, RedisJobService redisJobServ, long jobId, int jobType, String remoteFilePath){
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
    }
}
