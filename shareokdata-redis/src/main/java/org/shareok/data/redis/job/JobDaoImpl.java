/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.RedisUtil;
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
                    operations.opsForHash().put("job:"+jobId, "status", "1");
                    operations.opsForHash().put("job:"+jobId, "type", jobTypeStr);
                    operations.opsForHash().put("job:"+jobId, "repoType", repoTypeStr);
                    operations.opsForHash().put("job:"+jobId, "startTime", (null != startTimeStr ? RedisUtil.getRedisDateFormat().format(startTimeStr) : RedisUtil.getRedisDateFormat().format(new Date())));
                    operations.opsForHash().put("job:"+jobId, "endTime", "");
                    
                    operations.boundSetOps(uidStr).add(jobId);
                    
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
        List<RedisJob> jobs = new ArrayList<RedisJob>();
        try{
            BoundSetOperations<String, String> jobOps = (BoundSetOperations<String, String>) redisTemplate.boundSetOps(String.valueOf(uid));
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
            String time = (String)jobOps.get("startTime");
            job.setStartTime(RedisUtil.getRedisDateFormat().parse(time));
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
}
