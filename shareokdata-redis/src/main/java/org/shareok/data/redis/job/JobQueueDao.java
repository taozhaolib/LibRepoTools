/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.List;

/**
 *
 * @author Tao Zhao
 */
public interface JobQueueDao {
    public boolean isJobQueueEmpty(String queueName);
    public List getJobQueueByName(String queueName);
    public void addJobIntoQueue(long jobId, String queueName);
    public long removeJobFromQueue(String queueName);
//    public void saveJobQueue(final JobQueue jobQueue);
}
