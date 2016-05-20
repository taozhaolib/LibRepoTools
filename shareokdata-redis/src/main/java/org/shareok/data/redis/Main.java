/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import java.util.Date;
import java.util.List;
import org.shareok.data.redis.job.JobDao;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        UserDaoImpl impl = (UserDaoImpl) context.getBean("userDaoImpl");
        
        RedisUser user = impl.findUserByUserEmail("test@gmail.com");
//        RedisUser user = (RedisUser) context.getBean("user");
//        user.setUserName("admin-libtools@ou.edu");
//        user.setEmail("tao.zhao.admin@ou.edu");
//        user.setPassword("admin");
////        user.setSessionKey(RedisUtil.getRandomString());
//        user.setStartTime(new Date());
        user.setRole(1);
        impl.updateUser(user);
//        impl.addUser(user);
//        RedisUser user = impl.findUserByUserEmail("tao.zhao@ou.edu");
      //  impl.deactivateUserByUserId(user.getUserId());
//        JobDao jobDao = RedisUtil.getJobDao();
//        long jobId = jobDao.startJob(20, 1, 1, null);
//        RedisJob job = jobDao.findJobByJobId(jobId);
//        System.out.print(job.getWorker() + " *** \n");
//        List<RedisJob> jobs = jobDao.getJobListByUser(20);
//        System.out.println("Redis template is working!");
    }
}
