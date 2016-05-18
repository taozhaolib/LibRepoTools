/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.Date;

/**
 *
 * @author Tao Zhao
 */
public class RedisJob {
    private Date startTime;
    private Date endTime;
    private long jobId;
    private long userId;
    private int type;
    private int repoType;
    private int status;

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public long getJobId() {
        return jobId;
    }

    public long getUserId() {
        return userId;
    }

    public int getType() {
        return type;
    }

    public int getRepoType() {
        return repoType;
    }

    public int getStatus() {
        return status;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    /**
     * The ID of the user who runs this job
     * @param userId 
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRepoType(int repoType) {
        this.repoType = repoType;
    }
    
}
