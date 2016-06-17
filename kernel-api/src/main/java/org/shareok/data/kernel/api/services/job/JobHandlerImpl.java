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
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.DataHandler;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.exceptions.EmptyUploadedPackagePathOfSshUploadJobException;
import org.shareok.data.kernel.api.services.DataService;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.beans.BeansException;
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
    public RedisJob execute(long uid, int jobType, int repoType, DataHandler handler, MultipartFile localFile, String remoteFilePath){
        try{
            ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");

            RedisJobService redisJobServ = (RedisJobService) context.getBean("redisJobServiceImpl");
            long jobId = redisJobServ.startJob(uid, jobType, repoType);

            String jobFilePath = ShareokdataManager.getJobReportPath(DataUtil.REPO_TYPES[repoType], DataUtil.JOB_TYPES[jobType], jobId);

            DataService ds = ServiceUtil.getDataService(context, jobType);

            String filePath = "";
            String reportFilePath = jobFilePath + File.separator + String.valueOf(jobId) + "-report.txt";

            if(null != localFile && !localFile.isEmpty()){
                filePath = ServiceUtil.saveUploadedFile(localFile, jobFilePath);
            }
            else if(null != remoteFilePath && !"".equals(remoteFilePath)){
                filePath = processRemoteFileByJobType(jobType, redisJobServ, jobFilePath, remoteFilePath);
            }

            handler.setUploadFile(filePath);
            handler.setReportFilePath(reportFilePath);
            ds.setHandler(handler);
            
            redisJobServ.updateJob(jobId, "status", "1"); 
            
            String jobReturnValue = ds.executeTask(DataUtil.JOB_TYPES[jobType]);
            processJobReturnValue(jobReturnValue, redisJobServ, jobId, jobType, remoteFilePath);
            
            redisJobServ.updateJob(jobId, "endTime", ShareokdataManager.getSimpleDateFormat().format(new Date()));
            return redisJobServ.findJobByJobId(jobId);
        }
        catch(BeansException | NumberFormatException | EmptyUploadedPackagePathOfSshUploadJobException ex){
            logger.error("Cannot exectue the job with type "+DataUtil.JOB_TYPES[jobType]+" for repository "+DataUtil.REPO_TYPES[repoType], ex);
        }
        return null;
    }
    
    @Override
    public RedisJob execute(long uid, String jobType, String repoType, DataHandler handler, MultipartFile localFile, String remoteFilePath){
        return execute(uid, Arrays.asList(DataUtil.JOB_TYPES).indexOf(jobType), Arrays.asList(DataUtil.REPO_TYPES).indexOf(repoType), handler, localFile, remoteFilePath);
    }
    
    private String processRemoteFileByJobType(int jobType, RedisJobService redisJobServ, String jobFilePath, String remoteFilePath) throws EmptyUploadedPackagePathOfSshUploadJobException{
        String filePath = "";
        switch(jobType){
            case 5:
                return "uri--" + remoteFilePath;                
            case 4:
                long oldJobId = Long.parseLong(remoteFilePath);
                Map values = redisJobServ.getJobInfoByAttributes(oldJobId, new String[]{"uploadedPackagePath", "imported"});
                if(null != values.get("imported") && "true".equals((String)values.get("imported"))){
                    return null;
                }
                Object obj = values.get("uploadedPackagePath");
                if(null == obj || "".equals((String)obj)){
                    throw new EmptyUploadedPackagePathOfSshUploadJobException("The ssh-upload job does NOT have a path to the uploaded package in the server.");
                }
                filePath = (String)obj;
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
                        break;
                    case 4:
                        long oldJobId = Long.parseLong(remoteFilePath);
                        redisJobServ.updateJob(oldJobId, "status", "5");
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
