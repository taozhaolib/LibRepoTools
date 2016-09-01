/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services;

import org.shareok.data.datahandlers.JobHandler;
import org.shareok.data.redis.job.RedisJob;

/**
 *
 * @author Tao Zhao
 */
public interface DataService extends Runnable {
    public void setUserId(long userId);
    public void setHandler(JobHandler handler);
    public void loadJobInfoByJob(RedisJob job);
    public String executeTask(String jobType);
    public JobHandler getHandler();
}
