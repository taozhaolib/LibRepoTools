/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.shareok.data.redis.job.JobDao;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Tao Zhao
 */
public class RedisJobServiceImpl implements RedisJobService {
    
    @Autowired
    @Qualifier("jobDaoImpl")
    private JobDao jobDao;

    @Override
    public long startJob(long uid, int jobType, int repoType, int serverId) {
        return jobDao.startJob(uid, jobType, repoType, serverId);
    }

    @Override
    public long startJob(long uid, int jobType, int repoType, int serverId, Date startTime) {
        return jobDao.startJob(uid, jobType, repoType, serverId, startTime);
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

    @Override
    public void updateJobInfoByJobType(long jobId, String jobType, Map values) {
        jobDao.updateJobInfoByJobType(jobId, jobType, values);
    }

    @Override
    public Map<String, String> getJobInfoByAttributes(long jobId, String[] jobAttributes) {
        return jobDao.getJobInfoByAttributes(jobId, jobAttributes);
    }
    
    @Override
    public RedisJob createJob(final long uid, final int jobType, final Map<String, String> values){
        return jobDao.createJob(uid, jobType, values);
    }

    @Override
    public RedisJob saveJob(RedisJob job) {
        return jobDao.saveJob(job);
    }

    @Override
    public Map<String, String> getReportData(RedisJob job) {
        return job.getData();
    }
    
}
