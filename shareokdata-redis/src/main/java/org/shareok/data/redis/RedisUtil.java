/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.job.DspaceApiJob;
import org.shareok.data.redis.job.JobDao;
import org.shareok.data.redis.job.JobDaoImpl;
import org.shareok.data.redis.job.RedisJob;
import org.shareok.data.redis.server.DspaceRepoServer;
import org.shareok.data.redis.server.IslandoraRepoServer;
import org.shareok.data.redis.server.RepoServer;
import org.shareok.data.redis.server.RepoServerDaoImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */


public class RedisUtil {
    
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class);
    
    public static String[] REDIS_JOB_STATUS= {"undecided", "running", "completed", "failed", "built", "imported", "uploaded", "queued", "created"};
    

    /**
     *This works by choosing 130 bits from a cryptographically secure random bit generator, and encoding them in base-32. 128 bits is considered to be cryptographically strong, 
     * but each digit in a base 32 number can encode 5 bits, so 128 is rounded up to the next multiple of 5. 
     * This encoding is compact and efficient, with 5 random bits per character. Compare this to a random UUID, 
     * which only has 3.4 bits per character in standard layout, and only 122 random bits in total.
     * 
     * If you allow session identifiers to be easily guessable (too short, flawed random number generator, etc.), 
     * attackers can hijack other's sessions. Note that SecureRandom objects are expensive to initialize, so you'll want to keep one around and reuse it.
     * 
     * @return a secure, random string
     */
    public static String getRandomString(){
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
    
    public static String getUserQueryKey(long uid){
        return ShareokdataManager.getRedisUserIdQueryPrefix()+String.valueOf(uid);
    }
    
    public static String getJobQueryKey(long jobId){
        return ShareokdataManager.getRedisJobQueryPrefix()+String.valueOf(jobId);
    }
    
    public static UserDao getUserDao(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (UserDaoImpl) context.getBean("userDaoImpl");
    }
    
    public static JobDao getJobDao(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (JobDaoImpl) context.getBean("jobDaoImpl");
    }
    
    public static RedisUser getUserInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (RedisUser) context.getBean("user");
    }
    
    public static RedisJob getJobInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (RedisJob) context.getBean("job");
    }
    
    public static DspaceApiJob getDspaceApiJobInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (DspaceApiJob) context.getBean("dspaceApiJob");
    }
    
    public static void sortJobList(List<RedisJob> jobList){
        jobList.sort(new Comparator<RedisJob>(){
            @Override
            public int compare(RedisJob o1, RedisJob o2) {
                int result = 0;
                try{
                    if(null != o1 && null != o2){
                        result = Math.toIntExact(o1.getJobId() - o2.getJobId());
                    }
                }
                catch(UnsupportedOperationException uoex){
                    logger.error("Cannot compare the tow job objects", uoex);
                }
                return result;
            }
        });
    }
    
    public static String getJobQueueName(long userId, String jobType, String serverName){
        return String.valueOf(userId)+"--"+serverName+"--"+jobType;
    }
    
    public static String getServerQueryKey(int serverId){
        return ShareokdataManager.getRedisServerQueryPrefix() + String.valueOf(serverId);
    }
    
    public static RepoServer getServerInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (RepoServer) context.getBean("repoServer");
    }
    
    public static RepoServerDaoImpl getServerDaoInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (RepoServerDaoImpl) context.getBean("repoServerDaoImpl");
    }
    
    public static IslandoraRepoServer getIslandoraRepoServerInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (IslandoraRepoServer) context.getBean("islandoraRepoServer");
    }
    
    public static DspaceRepoServer getDspaceRepoServerInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        return (DspaceRepoServer) context.getBean("dspaceRepoServer");
    }
}
