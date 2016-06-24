/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

import java.util.List;
import org.shareok.data.redis.job.JobQueueDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class JobQueueServiceImpl implements JobQueueService{
    
    private JobQueueDao jobQueueDao;

    @Autowired
    public void setJobQueueDao(JobQueueDao jobQueueDao) {
        this.jobQueueDao = jobQueueDao;
    }
    
    @Override
    public boolean isJobQueueEmpty(String queueName) {
        return jobQueueDao.isJobQueueEmpty(queueName);
    }

    @Override
    public List getJobQueueByName(String queueName) {
        return jobQueueDao.getJobQueueByName(queueName);
    }

    @Override
    public void addJobIntoQueue(long jobId, String queueName) {
        jobQueueDao.addJobIntoQueue(jobId, queueName);
    }

    @Override
    public long removeJobFromQueue(String queueName) {
        return jobQueueDao.removeJobFromQueue(queueName);
    }
    
}
