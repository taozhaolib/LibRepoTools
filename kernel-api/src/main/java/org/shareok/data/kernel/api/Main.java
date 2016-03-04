/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api;

import java.util.Date;
import org.shareok.data.kernel.api.services.user.RedisUserServiceImpl;
import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.RedisUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
        RedisUser user = new RedisUser();
        user.setUserName("tao.zhao.test2@ou.edu");
        user.setEmail("tao.zhao.test2@ou.edu");
        user.setPassword("12345");
        user.setSessionKey(RedisUtil.getRandomString());
        user.setStartTime(new Date());
        RedisUserServiceImpl impl = (RedisUserServiceImpl) context.getBean("redisUserServiceImpl");
        impl.addUser(user);
    }
}
