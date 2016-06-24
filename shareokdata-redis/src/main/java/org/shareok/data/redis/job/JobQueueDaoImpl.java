/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.util.ArrayList;
import java.util.List;
import org.shareok.data.config.ShareokdataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 *  Note:
 *      The redis database is designed to be thread safe (actually single thread), i.g., only one writing can happen at at time
 * 
 * @author Tao Zhao
 */
public class JobQueueDaoImpl implements JobQueueDao {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JobDaoImpl.class);
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override
    public List getJobQueueByName(String queueName) {
        List jobs = new ArrayList();
        try{
            BoundListOperations<String, String> jobQueueOps = (BoundListOperations<String, String>) redisTemplate.boundListOps(queueName);
            jobs = jobQueueOps.range(0, ShareokdataManager.getRedisJobQueueMaxJobs());                     
        }
        catch(Exception ex){
            logger.error("Cannot get the list of the jobs stored in " + queueName, ex);
        }        
        return jobs;
    }

    @Override
    public void addJobIntoQueue(long jobId, String queueName) {
        try{
            BoundListOperations<String, String> jobQueueOps = (BoundListOperations<String, String>) redisTemplate.boundListOps(queueName);
            jobQueueOps.rightPush(String.valueOf(jobId));
        }
        catch(Exception ex){
            logger.error("Cannot add job "+String.valueOf(jobId)+" into "+queueName, ex);
        }
    }

    @Override
    public long removeJobFromQueue(String queueName) {
        try{
            BoundListOperations<String, String> jobQueueOps = (BoundListOperations<String, String>) redisTemplate.boundListOps(queueName);
            return Long.valueOf(jobQueueOps.leftPop());
        }
        catch(Exception ex){
            logger.error("Cannot pop a job from "+queueName, ex);
        }
        return -1;
    }

//    @Override
//    public void saveJobQueue(final JobQueue jobQueue) {
//        String[] queueStr = jobQueue.getQueue().toArray(new String[jobQueue.getQueue().size()]);
//        final StringBuilder sb = new StringBuilder();
//        sb.append(queueStr[0]);
//        if(queueStr.length > 1){
//            for(int i = 1; i < queueStr.length; i++){
//                sb.append("," + queueStr[i]);
//            }
//        }
//        List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
//                @Override
//                public List<Object> execute(RedisOperations operations) throws DataAccessException {
//                    operations.multi();
//                    operations.boundListOps(jobQueue.getName());
//                    operations.;
////                    operations.opsForHash().put("job:"+jobId, "jobId", jobId);
////                    operations.opsForHash().put("job:"+jobId, "status", "4");
////                    operations.opsForHash().put("job:"+jobId, "type", jobTypeStr);
////                    operations.opsForHash().put("job:"+jobId, "repoType", repoTypeStr);
////                    operations.opsForHash().put("job:"+jobId, "startTime", (null != startTimeStr ? ShareokdataManager.getSimpleDateFormat().format(startTimeStr) : ShareokdataManager.getSimpleDateFormat().format(new Date())));
////                    operations.opsForHash().put("job:"+jobId, "endTime", "");
////                    
////                    operations.boundSetOps("user_"+uidStr+"_job_set").add(jobId);
//                    
//                    List<Object> jobList= operations.exec();
//                    if(!jobList.get(0).equals(true)){
//                        operations.discard();
//                    }
//                    return jobList;
//                }
//            });
//    }

    @Override
    public boolean isJobQueueEmpty(String queueName) {
        try{
            BoundListOperations<String, String> jobQueueOps = (BoundListOperations<String, String>) redisTemplate.boundListOps(queueName);
            return jobQueueOps.range(0, ShareokdataManager.getRedisJobQueueMaxJobs()).isEmpty();                     
        }
        catch(Exception ex){
            logger.error("Cannot determine if the job queue " + queueName + " is empty!", ex);
        }        
        return false;
    }
    
}
