/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.islandora;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import org.shareok.data.datahandlers.JobHandler;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.dspacemanager.DspaceSshHandler;
import org.shareok.data.islandoramanager.IslandoraSshDataUtil;
import org.shareok.data.islandoramanager.IslandoraSshHandler;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.job.JobQueueService;
import org.shareok.data.kernel.api.services.job.RedisJobService;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.JobDao;
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
public class IslandoraSshServiceImpl implements IslandoraSshService {
    
    private IslandoraSshHandler handler; 
    private JobQueueService jobQueueService;
    private RedisJobService jobService;
    private long userId;

    public long getUserId() {
        return userId;
    }

    public JobQueueService getJobQueueService() {
        return jobQueueService;
    }

    @Override
    public IslandoraSshHandler getHandler() {
        return handler;
    }

    @Autowired
    public void setJobQueueService(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }

    @Autowired
    @Qualifier("redisJobServiceImpl")
    public void setJobService(RedisJobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public String importIslandora() {
        return handler.importIslandora();
    }

    @Override
    @Autowired   
    @Qualifier("islandoraSshHandler")
    public void setHandler(JobHandler handler) {
        this.handler = (IslandoraSshHandler)handler;
        if(null == this.handler.getSshExec()){
            this.handler.setSshExec(IslandoraSshDataUtil.getSshExecForIslandora());
        }
    }

    @Override
    public String executeTask(String jobType) {
        switch (jobType) {
            case "ssh-import-islandora":
                return handler.importIslandora();
            default:
                return null;
        }
    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
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
                ApplicationContext context = new ClassPathXmlApplicationContext("islandoraManagerContext.xml");
                handler = (IslandoraSshHandler)context.getBean("islandoraSshHandler");
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
        long jobId = job.getJobId();
        int jobType = job.getType();
        handler.setJobType(job.getType());
        String jobFilePath = ShareokdataManager.getJobReportPath(DataUtil.JOB_TYPES[jobType], jobId);
        handler.setReportFilePath(jobFilePath + File.separator + String.valueOf(jobId) + "-report.txt");
        handler.setServerId(String.valueOf(job.getServerId()));
        String schema = (String)DataUtil.JOB_TYPE_DATA_SCHEMA.get(DataUtil.JOB_TYPES[job.getType()]);
        Map data = RedisUtil.getJobDao().getJobInfoByAttributes(jobId, schema.split(","));
        for(Field f : IslandoraSshHandler.class.getDeclaredFields()){
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
