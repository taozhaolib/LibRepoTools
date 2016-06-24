/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.islandora;

import org.shareok.data.config.DataHandler;
import org.shareok.data.config.DataUtil;
import org.shareok.data.dspacemanager.DspaceSshHandler;
import org.shareok.data.islandoramanager.IslandoraSshDataUtil;
import org.shareok.data.islandoramanager.IslandoraSshHandler;
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

    @Autowired
    public void setJobQueueService(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }

    @Autowired
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
    public void setHandler(DataHandler handler) {
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
            handler.loadJobInfoByJobId(jobId);
            String jobReturnValue = executeTask(jobTypeStr);
            ServiceUtil.processJobReturnValue(jobReturnValue, job);
//            System.out.println(" The job "+String.valueOf(jobId)+" has been processed! filePath = "+job.getFilePath());
        }
        Thread.currentThread().interrupt();
    }
    
}
