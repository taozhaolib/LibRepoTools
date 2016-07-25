/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public class RedisJob {
    private Date startTime;
    private Date endTime;
    private long jobId;
    private long userId;
    private int serverId;
    private int type;
    private int repoType;
    private int status;
    private String filePath;
    private Map<String, String> data;

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

    public int getServerId() {
        return serverId;
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

    public String getFilePath() {
        return filePath;
    }

    public Map<String, String> getData() {
        return data;
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

    public void setServerId(int serverId) {
        this.serverId = serverId;
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

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
    
}
