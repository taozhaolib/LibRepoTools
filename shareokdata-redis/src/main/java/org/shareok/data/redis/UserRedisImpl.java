/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import java.net.URL;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.transaction.annotation.Transactional;

import org.shareok.data.config.ShareokdataManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.BoundHashOperations;

/**
 *
 * @author Tao Zhao
 */
public class UserRedisImpl implements UserRedis {
    
    private static String USER_KEY = "User";
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // inject the template as ListOperations
    @Resource(name="redisTemplate")
    private ListOperations<String, String> listOps;
    
    @Override
    public User addUser(User user){
        RedisTemplate<String, Object> redis = new RedisTemplate<>();
        stringRedisTemplate.setConnectionFactory(connectionFactory);        
        stringRedisTemplate.afterPropertiesSet();
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.multi();  
        RedisAtomicLong userIdIndex = new RedisAtomicLong(ShareokdataManager.getRedisGlobalUidSchema(), stringRedisTemplate.getConnectionFactory());
        BoundHashOperations<String, String, String> userOps = stringRedisTemplate.boundHashOps("uid:"+userIdIndex);
        userOps.put("userName", (null != user.getUserName() && !user.getUserName().equals("")) ? user.getUserName() : user.getEmail());
        userOps.put("password", user.getPassword());
        userOps.put("email", user.getEmail());
        userOps.put("sessionKey", user.getSessionKey());
        stringRedisTemplate.exec(); 
        stringRedisTemplate.setEnableTransactionSupport(false);
        user.setUserId(userIdIndex.longValue());
        return user;
    }
    
    @Override
    public User updateUser(User user){
        return user;
    }
    
    @Override
    public User findUserByUserId(long userId){
        BoundHashOperations<String, String, String> userOps = stringRedisTemplate.boundHashOps("uid:"+userId);
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        User user = (User) context.getBean("user");
        user.setEmail(userOps.get("email"));
        user.setUserName(userOps.get("userName"));
        user.setUserId(Long.parseLong(userOps.get("userId")));
        user.setPassword(userOps.get("password"));
        user.setSessionKey(userOps.get("sessionKey"));
        return user;
    }
    
    @Override
    public User deleteUserByUserId(long userId){
        return null;
    }

    public void addLink(String userId, String url) {
      listOps.leftPush(userId, url);
    }
    
    @Transactional
    public void test2(){
        RedisTemplate<String, Object> redis = new RedisTemplate<>();
        redis.setConnectionFactory(connectionFactory);
//        redis.setKeySerializer(ApplicationConfig.StringSerializer.INSTANCE);
//        redis.setValueSerializer(new JacksonJsonRedisSerializer<User>(UserRedisImpl.class));
        redis.afterPropertiesSet();
        redis.setEnableTransactionSupport(true);//奇怪的是一定要再显示开启redistemplate的事务支持
        redis.multi();  
        redis.boundValueOps("somevkey").increment(1);  
        redis.boundZSetOps("somezkey").add("zvalue", 11);  
        redis.exec(); 
        redis.setEnableTransactionSupport(false);
    }

}
