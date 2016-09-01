/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.shareok.data.config.JobHandler;
import org.shareok.data.config.DataUtil;
import org.shareok.data.dspacemanager.DspaceApiHandler;
import org.shareok.data.kernel.api.exceptions.IncompleteHandlerInfoException;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.job.RedisJobService;
import org.shareok.data.redis.job.DspaceApiJob;
import org.shareok.data.redis.job.JobDaoImpl;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Tao Zhao
 */
public class DspaceRestServiceImpl implements DspaceRestService {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DspaceRestServiceImpl.class);
    
    private DspaceApiHandler handler;
    private RedisJobService jobService;
    private long jobId;
    private long userId;

    @Override
    public DspaceApiHandler getHandler() {
        return handler;
    }

    public RedisJobService getJobService() {
        return jobService;
    }

    public long getJobId() {
        return jobId;
    }

    public long getUserId() {
        return userId;
    }
    
    @Override
    public String loadItemsFromSafPackage() {
        String loadJson = null;
        Map<String, List<String>>loadResultMap = handler.loadItemsFromSafPackage();
        try {        
            loadJson = new ObjectMapper().writeValueAsString(loadResultMap);
        } catch (JsonProcessingException ex) {
            logger.error("Cannot convert the Map of loading results into JSON String", ex);
        }
        return loadJson;
    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    @Autowired
    public void setJobService(RedisJobService jobService) {
        this.jobService = jobService;
    }

    @Override
    @Autowired
    @Qualifier("dspaceApiHandlerImpl")
    public void setHandler(JobHandler handler) {
        this.handler = (DspaceApiHandler)handler;
    }

    @Override
    public void loadJobInfoByJob(RedisJob job) {
        jobId = job.getJobId();
        userId = job.getUserId();
        handler.setJob((DspaceApiJob) job);
    }

    @Override
    public String executeTask(String jobType) {
        if("rest-import-dspace".equals(jobType)){
            return loadItemsFromSafPackage();
        }
        return null;
    }

    @Override
    public void run() {
        try{
            DspaceApiJob job = handler.getJob();
            if(null == handler || null == job){
                throw new IncompleteHandlerInfoException("Handler or Job is null!");
            }
            jobService.updateJob(jobId, "status", "1");
            loadJobInfoByJob(job);
            // Start executing:
            String jobReturnValue = executeTask(DataUtil.JOB_TYPES[job.getType()]);
            
            ServiceUtil.processJobReturnValue(jobReturnValue, job);
            Thread.currentThread().interrupt();
            jobService.updateJob(jobId, "status", "2");
        }
        catch(IncompleteHandlerInfoException ex){
            jobService.updateJob(jobId, "status", "3");
        }
    }
    
}
