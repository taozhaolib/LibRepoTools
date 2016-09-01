/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import org.shareok.data.config.JobHandler;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.dspacemanager.DspaceSshDataUtil;
import org.shareok.data.dspacemanager.DspaceSshHandler;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.job.JobQueueService;
import org.shareok.data.kernel.api.services.job.RedisJobService;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */
@Service
public class DspaceSshServiceImpl implements DspaceSshService {
    private DspaceSshHandler handler;
    private JobQueueService jobQueueService;
    private RedisJobService jobService;
    private long userId;

    @Override
    public DspaceSshHandler getHandler() {
        return handler;
    }

    public long getUserId() {
        return userId;
    }

    public JobQueueService getJobQueueService() {
        return jobQueueService;
    }

    @Autowired
    public void setJobService(RedisJobService jobService) {
        this.jobService = jobService;
    }

    @Autowired
    public void setJobQueueService(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    @Autowired
    @Qualifier("dspaceSshHandler")
    public void setHandler(JobHandler handler) {
        this.handler = (DspaceSshHandler)handler;
        if(null == this.handler.getSshExec()){
            this.handler.setSshExec(DspaceSshDataUtil.getSshExecForDspace());
        }
    }
    
    @Override
    public String sshImportData(){
        return handler.importDspace();
    }
    
    @Override
    public String uploadSafDspace(){
        return handler.uploadSafDspace();
    }

    @Override
    public String executeTask(String jobType) {
        switch (jobType) {
            case "ssh-import-dspace":
                return handler.importDspace();
            case "ssh-upload-dspace":
                return handler.uploadSafDspace();
            case "ssh-importloaded-dspace":
                return handler.importUploadedSafDspace();
            default:
                return null;
        }
    }

    @Override
    public void run() {
        String jobTypeStr = DataUtil.JOB_TYPES[handler.getJobType()];
        String queueName = RedisUtil.getJobQueueName(getUserId(), jobTypeStr, handler.getServerName());    

        while(!Thread.currentThread().isInterrupted() && !jobQueueService.isJobQueueEmpty(queueName)){
            long jobId = jobQueueService.removeJobFromQueue(queueName);
            RedisJob job = jobService.findJobByJobId(jobId);
            jobService.updateJob(jobId, "status", "1");
            if(null == handler){
                ApplicationContext context = new ClassPathXmlApplicationContext("dspaceManagerContext.xml");
                handler = (DspaceSshHandler)context.getBean("dspaceSshHandler");
            }
            loadJobInfoByJob(job);
            String jobReturnValue = executeTask(jobTypeStr);
            ServiceUtil.processJobReturnValue(jobReturnValue, job);
//            System.out.println(" The job "+String.valueOf(jobId)+" has been processed! filePath = "+job.getFilePath());
        }
        Thread.currentThread().interrupt();
    }

    @Override
    public void loadJobInfoByJob(RedisJob job) {
        int jobType = job.getType();
        long jobId = job.getJobId();
        handler.setJobType(job.getType());
        String jobFilePath = ShareokdataManager.getJobReportPath(DataUtil.JOB_TYPES[jobType], jobId);
        handler.setReportFilePath(jobFilePath + File.separator + String.valueOf(jobId) + "-report.txt");
        handler.setServerId(String.valueOf(job.getServerId()));
        String schema = (String)DataUtil.JOB_TYPE_DATA_SCHEMA.get(DataUtil.JOB_TYPES[job.getType()]);
        Map data = RedisUtil.getJobDao().getJobInfoByAttributes(jobId, schema.split(","));
        for(Field f : DspaceSshHandler.class.getDeclaredFields()){
            try {
                String fieldName = f.getName();
                if(data.containsKey(fieldName)){
                    f.set(this, (String)data.get(fieldName));
                }
            } catch (SecurityException ex) {
            } catch (IllegalArgumentException | IllegalAccessException ex) {
            }            
        }
    }
    
}
