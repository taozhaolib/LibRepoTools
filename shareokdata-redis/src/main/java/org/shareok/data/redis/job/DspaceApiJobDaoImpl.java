/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

import java.text.ParseException;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 *
 * @author Tao Zhao
 */
public class DspaceApiJobDaoImpl extends JobDaoImpl {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JobDaoImpl.class);
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override
    public DspaceApiJob findJobByJobId(long jobId){
        try{
        BoundHashOperations<String, String, String> jobOps = redisTemplate.boundHashOps(RedisUtil.getJobQueryKey(jobId));
        if(null != jobOps){
            DspaceApiJob job = RedisUtil.getDspaceApiJobInstance();
            job.setJobId(jobId);
            String startTime = (String)jobOps.get("startTime");
            job.setStartTime((null == startTime || "".equals(startTime)) ? null : ShareokdataManager.getSimpleDateFormat().parse(startTime));
            String endTime = (String)jobOps.get("endTime");
            job.setEndTime((null == endTime || "".equals(endTime)) ? null : ShareokdataManager.getSimpleDateFormat().parse(endTime));
            job.setUserId(Long.valueOf(jobOps.get("userId")));
            job.setServerId(Integer.valueOf(jobOps.get("serverId")));
            job.setStatus(Integer.valueOf(jobOps.get("status")));
            job.setType(Integer.valueOf(jobOps.get("type")));
//            job.setRepoType(Integer.valueOf(jobOps.get("repoType")));
            job.setFilePath(jobOps.get("filePath"));
            job.setCommunityId((String)jobOps.get("communityId"));
            job.setSubCommunityId((String)jobOps.get("subCommunityId"));
            job.setCollectionId((String)jobOps.get("collectionId"));
            job.setItemId((String)jobOps.get("itemId"));
            job.setBitstreamId((String)jobOps.get("bitstreamId"));
            job.setPolicyId((String)jobOps.get("policyId"));
            return job;
        }
        else{
            return null;
        }
        }
        catch(ParseException | NumberFormatException ex){
            logger.error("Cannot find the job information by job ID "+jobId, ex);
        }
        return null;
    }
}
