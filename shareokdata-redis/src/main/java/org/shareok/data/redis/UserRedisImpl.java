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
    public void addUser(User user){
        RedisTemplate<String, Object> redis = new RedisTemplate<>();
        redis.setConnectionFactory(connectionFactory);
        RedisAtomicLong userIdIndex = new RedisAtomicLong(Shareok);
        redis.afterPropertiesSet();
        redis.setEnableTransactionSupport(true);//奇怪的是一定要再显示开启redistemplate的事务支持
        redis.multi();  
        redis.boundValueOps("somevkey").increment(1);  
        redis.boundZSetOps("somezkey").add("zvalue", 11);  
        redis.exec(); 
        redis.setEnableTransactionSupport(false);
        stringRedisTemplate.opsForHash().put(USER_KEY, user., this);
    }
    
    @Override
    public User updateUser(User user){
        
    }
    
    @Override
    public User findUserByUserId(int userId){
        
    }
    
    @Override
    public User deleteUserByUserId(int userId){
        
    }
    
    public void enableTransactionSupport(){
        template.setEnableTransactionSupport(true);
    }
    
    public void disableTransactionSupport(){
        template.setEnableTransactionSupport(false);
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
