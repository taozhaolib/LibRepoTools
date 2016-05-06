/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Tao Zhao
 */
public interface JobDao {
    public long startJob(long uid, int jobType, int repoType);
    public long startJob(long uid, int jobType, int repoType, Date startTime);
    public void endJob(long jobId);
    public String getJobStatus(long jobId);
    public String getJobLogging(long jobId);
    public List<RedisJob> getJobListByUser(long uid);
    public List<RedisJob> getJobListByUserEmail(String email);
    public RedisJob findJobByJobId(long jobId);
}
