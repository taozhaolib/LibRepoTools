/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.exceptions.EmptyJobInfoException;
import org.shareok.data.redis.exceptions.EmptyJobTypeException;
import org.shareok.data.redis.exceptions.NonExistingUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Tao Zhao
 */
public class JobDaoImpl implements JobDao {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JobDaoImpl.class);
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override    
    public long startJob(long uid, int jobType, int repoType){
        return startJob(uid, jobType, repoType, null);
    }
    
    @Override
    @Transactional
    public long startJob(long uid, int jobType, int repoType, Date startTime){
        
        long jobIdCount = -1;
        
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            
            RedisAtomicLong jobIdIndex = new RedisAtomicLong(ShareokdataManager.getRedisGlobalJobIdSchema(), redisTemplate.getConnectionFactory());
            
            jobIdCount = jobIdIndex.incrementAndGet();
            final String jobId = String.valueOf(jobIdCount);
            final String uidStr = String.valueOf(uid);
            final String jobTypeStr = String.valueOf(jobType);
            final String repoTypeStr = String.valueOf(repoType);
            final Date startTimeStr = startTime;
            
            List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.boundHashOps("job:"+jobId);
                    operations.opsForHash().put("job:"+jobId, "userId", uidStr);
                    operations.opsForHash().put("job:"+jobId, "jobId", jobId);
                    operations.opsForHash().put("job:"+jobId, "status", "4");
                    operations.opsForHash().put("job:"+jobId, "type", jobTypeStr);
                    operations.opsForHash().put("job:"+jobId, "repoType", repoTypeStr);
                    operations.opsForHash().put("job:"+jobId, "startTime", (null != startTimeStr ? ShareokdataManager.getSimpleDateFormat().format(startTimeStr) : ShareokdataManager.getSimpleDateFormat().format(new Date())));
                    operations.opsForHash().put("job:"+jobId, "endTime", "");
                    
                    operations.boundSetOps("user_"+uidStr+"_job_set").add(jobId);
                    
                    List<Object> jobList= operations.exec();
                    if(!jobList.get(0).equals(true)){
                        operations.discard();
                    }
                    return jobList;
                }
            });
        }
        catch(Exception ex){
            logger.error("Cannot start a new job.", ex);
        }
        return jobIdCount;
    }
    
    @Override
    public void endJob(long jobId){
        
    }
    
    @Override
    public void updateJob(long jobId, String jobInfoType, String value){
        try{
            //RedisJob job = findJobByJobId(jobId);
            BoundHashOperations<String, String, String> jobOps = redisTemplate.boundHashOps(RedisUtil.getJobQueryKey(jobId));
            jobOps.put(jobInfoType, value);
        }
        catch(Exception ex){
            logger.error("Cannot update job info @ " + jobInfoType + " with value = " + value, ex);
        }
    }
    
    @Override
    public String getJobStatus(long jobId){
        try{
            BoundHashOperations<String, String, String> jobOps = redisTemplate.boundHashOps(RedisUtil.getJobQueryKey(jobId));
            return RedisUtil.REDIS_JOB_STATUS[Integer.valueOf(jobOps.get("status"))];
        }
        catch(Exception ex){
            logger.error("Cannot get job status information.", ex);
            return null;
        }
    }
    
    @Override
    public String getJobLogging(long jobId){
        return null;
    }
    
    @Override
    public List<RedisJob> getJobListByUser(long uid)
    {
        List<RedisJob> jobs = new ArrayList<>();
        try{
            BoundSetOperations<String, String> jobOps = (BoundSetOperations<String, String>) redisTemplate.boundSetOps("user_"+String.valueOf(uid)+"_job_set");
            Set<String> jobIds = jobOps.members();
            if(null != jobIds){                
                for(String jobIdStr : jobIds){
                    jobs.add(findJobByJobId(Long.valueOf(jobIdStr)));
                }
            }
            else{
                return null;
            }            
        }
        catch(Exception ex){
            logger.error("Cannot get the list of the jobs conducted by user " + uid, ex);
        }
        RedisUtil.sortJobList(jobs);
        return jobs;
    }
    
    @Override
    public List<RedisJob> getJobListByUserEmail(String email){        
        try{
            RedisUser user = RedisUtil.getUserDao().findUserByUserEmail(email);
            if(null != user){
                getJobListByUser(user.getUserId());
            }
            else throw new NonExistingUserException("Cannot find user by email!");
        }
        catch(NonExistingUserException ex){
            logger.error("Cannot get the user by user email "+email, ex);
        }
        return null;
    }
    
    @Override
    public RedisJob findJobByJobId(long jobId){
        try{
        BoundHashOperations<String, String, String> jobOps = redisTemplate.boundHashOps(RedisUtil.getJobQueryKey(jobId));
        if(null != jobOps){
            RedisJob job = RedisUtil.getJobInstance();
            job.setJobId(jobId);
            String startTime = (String)jobOps.get("startTime");
            job.setStartTime(ShareokdataManager.getSimpleDateFormat().parse(startTime));
            String endTime = (String)jobOps.get("endTime");
            job.setEndTime(ShareokdataManager.getSimpleDateFormat().parse(endTime));
            job.setUserId(Long.valueOf(jobOps.get("userId")));
            job.setStatus(Integer.valueOf(jobOps.get("status")));
            job.setType(Integer.valueOf(jobOps.get("type")));
            job.setRepoType(Integer.valueOf(jobOps.get("repoType")));
            return job;
        }
        else{
            return null;
        }
        }
        catch(Exception ex){
            logger.error("Cannot find the job information by job ID "+jobId, ex);
        }
        return null;
    }
    
    @Override
    public void updateJobInfoByJobType(long jobId, String jobType, Map values){
        
        try{
            if(null == jobType || "".equals(jobType)){
                throw new EmptyJobTypeException("Job type is empty for updating job info by job type.");
            }
//            RedisJob job = findJobByJobId(jobId);
            switch (jobType) {
                case "ssh-upload":
                case "ssh-import":
                    {
                        if(null == values || values.get("uploadedPackagePath") == null){
                            throw new EmptyJobInfoException("Job information is empty for updating job info by job type");
                        }       BoundHashOperations<String, String, String> jobOps = redisTemplate.boundHashOps(RedisUtil.getJobQueryKey(jobId));
                        jobOps.put("uploadedPackagePath", (String)values.get("uploadedPackagePath"));
                        if(jobType.equals("ssh-upload")){
                            jobOps.put("imported", "false");
                        }
                        break;
                    }
                case "ssh-import-uploaded":
                    {
                        if(null == values || values.get("uploading-jobId") == null){
                            throw new EmptyJobInfoException("Job information is empty for updating job info by job type");
                        }       String uploadingJobId = (String)values.get("uploading-jobId");
                        BoundHashOperations<String, String, String> jobOps = redisTemplate.boundHashOps(RedisUtil.getJobQueryKey(jobId));
                        jobOps.put("uploading-jobId", uploadingJobId);
                        updateJob(Long.parseLong(uploadingJobId), "imported", "true");
                        break;
                    }
            }
            
        }
        catch(EmptyJobTypeException | EmptyJobInfoException ex){
            logger.error("Cannot update job info for the job type "+jobType, ex);
        }
        
    }
    
    @Override
    public Map<String, String> getJobInfoByAttributes(long jobId, String[] jobAttributes){
        Map<String, String> jobInfo = new HashMap<>();
        try{
            for(String attr : jobAttributes){
                BoundHashOperations<String, String, String> jobOps = redisTemplate.boundHashOps(RedisUtil.getJobQueryKey(jobId));
                jobInfo.put(attr, (String)jobOps.get(attr));
            }
        }
        catch(Exception ex){
            logger.error("Cannot get the job info based on the job attributes", ex);
        }
        return jobInfo;
    }
}
