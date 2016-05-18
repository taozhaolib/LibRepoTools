/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

import java.util.Date;
import java.util.List;
import org.shareok.data.redis.job.JobDao;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class RedisJobServiceImpl implements RedisJobService {
    
    @Autowired
    private JobDao jobDao;

    @Override
    public long startJob(long uid, int jobType, int repoType) {
        return jobDao.startJob(uid, jobType, repoType);
    }

    @Override
    public long startJob(long uid, int jobType, int repoType, Date startTime) {
        return jobDao.startJob(uid, jobType, repoType, startTime);
    }
    
    @Override
    public void updateJob(long jobId, String jobInfoType, String value){
        jobDao.updateJob(jobId, jobInfoType, value);
    }

    @Override
    public List<RedisJob> getJobListByUser(long uid) {
        return jobDao.getJobListByUser(uid);
    }

    @Override
    public List<RedisJob> getJobListByUserEmail(String email) {
        return jobDao.getJobListByUserEmail(email);
    }

    @Override
    public RedisJob findJobByJobId(long jobId) {
        return jobDao.findJobByJobId(jobId);
    }
    
    @Override
    public void executeJob(long uid, int jobType, int repoType){
        
    }
    
}
