/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.DataHandler;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.services.DataService;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class JobHandlerImpl implements JobHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JobHandlerImpl.class);
    
    /**
     * 
     * @param uid : user ID
     * @param repoType : type of the repository
     * @param jobType : job type. Refer to the job type definitions
     * @param handler : handler of job execution
     * @param localFile : uploaded file from local computer
     * @param remoteFilePath : remote resource of the file
     * @return : path to the report file
     */
    @Override
    public RedisJob execute(long uid, int repoType, int jobType, DataHandler handler, MultipartFile localFile, String remoteFilePath){
        
        ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        
        RedisJobService redisJobServ = (RedisJobService) context.getBean("redisJobServiceImpl");
        long jobId = redisJobServ.startJob(uid, jobType, repoType);
        
        String jobFilePath = ShareokdataManager.getJobReportPath(DataUtil.REPO_TYPES[repoType], DataUtil.JOB_TYPES[jobType], jobId);
        
        //System.setProperty("logfile.path", jobFilePath + File.separator + String.valueOf(jobId) + ".log");
        
        logger.debug("sort kind of here");
        
        DataService ds = ServiceUtil.getDataService(context, repoType, jobType);
        
        String filePath = "";
        String reportFilePath = jobFilePath + File.separator + String.valueOf(jobId) + "-report.txt";
        
        if(null != localFile && !localFile.isEmpty()){
            filePath = ServiceUtil.saveUploadedFile(localFile, jobFilePath);
        }
        else if(null != remoteFilePath && !"".equals(remoteFilePath)){
            filePath = jobFilePath + File.separator + "downloaded.zip";
            ServiceUtil.downloadFile(remoteFilePath, filePath);
        }
        
        handler.setUploadFile(filePath);
        handler.setReportFilePath(reportFilePath);
        ds.setHandler(handler);
        redisJobServ.updateJob(jobId, "status", "1"); 
        String savedReportFilePath = ds.executeTask(DataUtil.JOB_TYPES[jobType]);
        if(null != savedReportFilePath && savedReportFilePath.equals(reportFilePath)){
            redisJobServ.updateJob(jobId, "status", "2");            
        }
        else{
            redisJobServ.updateJob(jobId, "status", "3");
        }
        redisJobServ.updateJob(jobId, "endTime", ShareokdataManager.getSimpleDateFormat().format(new Date()));
        return redisJobServ.findJobByJobId(jobId);
    }
    
    @Override
    public RedisJob execute(long uid, String repoType, String jobType, DataHandler handler, MultipartFile localFile, String remoteFilePath){
        return execute(uid, Arrays.asList(DataUtil.REPO_TYPES).indexOf(repoType), Arrays.asList(DataUtil.JOB_TYPES).indexOf(jobType), handler, localFile, remoteFilePath);
    }
}
