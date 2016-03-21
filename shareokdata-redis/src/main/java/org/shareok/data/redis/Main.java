/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import java.util.Date;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        UserRedisImpl impl = (UserRedisImpl) context.getBean("userRedisImpl");

//        RedisUser user = (RedisUser) context.getBean("user");
//        user.setUserName("tao.zhao.test2@ou.edu");
//        user.setEmail("tao.zhao.test2@ou.edu");
//        user.setPassword("12345");
//        user.setSessionKey(RedisUtil.getRandomString());
//        user.setStartTime(new Date());
        //user.setUserId(9);
        
        //impl.addUser(user);
        RedisUser user = impl.findUserByUserEmail("tao.zhao@ou.edu");
      //  impl.deactivateUserByUserId(user.getUserId());
        

        System.out.println("Redis template is working!");
    }
}
