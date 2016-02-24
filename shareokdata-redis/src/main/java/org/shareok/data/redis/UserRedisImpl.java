/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import org.shareok.data.config.ShareokdataManager;


/**
 *
 * @author Tao Zhao
 */
public class UserRedisImpl implements UserRedis {
    
    private static String USER_KEY = "User";
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;

    // inject the template as ListOperations
    @Resource(name="redisTemplate")
    private ListOperations<String, String> listOps;
    
    @Override
    @Transactional
    public User addUser(final User user){
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            RedisAtomicLong userIdIndex = new RedisAtomicLong(ShareokdataManager.getRedisGlobalUidSchema(), redisTemplate.getConnectionFactory());
            long uidCount = userIdIndex.incrementAndGet();
            final String uid = String.valueOf(uidCount);
            
            List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.boundHashOps("user:"+uid);
                    operations.opsForHash().put("user:"+uid, "userName", (null != user.getUserName() ? user.getUserName() : user.getEmail()));
                    operations.opsForHash().put("user:"+uid, "email", user.getEmail());
                    operations.opsForHash().put("user:"+uid, "userId", uid);
                    operations.opsForHash().put("user:"+uid, "password", user.getPassword());
                    operations.opsForHash().put("user:"+uid, "isActive", String.valueOf(true));
                    operations.opsForHash().put("user:"+uid, "sessionKey", (null != user.getSessionKey() ? user.getSessionKey() : RedisUtil.getRandomString()));
                    operations.opsForHash().put("user:"+uid, "startTime", (null != user.getStartTime() ? user.getStartTime().toString(): (new Date().toString())));
                    
                    operations.boundHashOps("users");
                    operations.opsForHash().put("users", user.getEmail(), uid);
                    
                    List<Object> userList= operations.exec();
                    if(!userList.get(0).equals(true)){
                        operations.discard();
                    }
                    return userList;
                }
            });
        }
        catch(Exception ex){
            Logger.getLogger(UserRedisImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    @Override
    public User updateUser(final User user){
        try{
            long uid = user.getUserId();
            redisTemplate.setConnectionFactory(connectionFactory);
            final String userKey = RedisUtil.getUserQueryKey(uid);
            List<Object> pipelinedResults = redisTemplate.executePipelined(new RedisCallback() {
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    String uid = String.valueOf(user.getUserId());
                    Map userInfo = redisTemplate.opsForHash().entries(userKey);
                    String oldUserEmail = (String)userInfo.get("email");
                    HashOperations operations = redisTemplate.opsForHash();
                    operations.put(userKey, "userName", (null != user.getUserName() ? user.getUserName() : user.getEmail()));
                    operations.put(userKey, "email", user.getEmail());
                    operations.put(userKey, "password", user.getPassword());
                    operations.put(userKey, "isActive", String.valueOf(true));
                    operations.put(userKey, "sessionKey", (null != user.getSessionKey() ? user.getSessionKey() : RedisUtil.getRandomString()));

                    if(!oldUserEmail.equals(user.getEmail())){
                        operations.delete("users", oldUserEmail);
                        operations.put("users", user.getEmail(), uid);
                    }
                    return null;
                }
            });
        }
        catch(Exception ex){
            Logger.getLogger(UserRedisImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    @Override
    public User findUserByUserId(long userId){
        BoundHashOperations<String, String, String> userOps = redisTemplate.boundHashOps(RedisUtil.getUserQueryKey(userId));
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
    public User findUserByUserEmail(String email){
        BoundHashOperations<String, String, String> userOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisUserNameIdMatchingTable());
        long userId = Long.valueOf(userOps.get(email));
        return findUserByUserId(userId);
    }
    
    @Override
    public void deleteUserByUserId(final long userId){
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            final String key = RedisUtil.getUserQueryKey(userId);
            Map userInfo = redisTemplate.opsForHash().entries(key);
            final String email = (String)userInfo.get("email");
            List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForHash().delete(ShareokdataManager.getRedisUserNameIdMatchingTable(), email);
                    operations.opsForHash().getOperations().delete(key);
                    
                    List<Object> opList= operations.exec();
                    if(!opList.get(0).equals(true) && !opList.get(0).equals(Long.valueOf(1))){
                        operations.discard();
                    }
                    return opList;
                }
            });
        }
        catch(Exception ex){
            Logger.getLogger(UserRedisImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deactivateUserByUserId(long userId){
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            final String key = RedisUtil.getUserQueryKey(userId);
            redisTemplate.opsForHash().put(key, "isActive", String.valueOf(false));
        }
        catch(Exception ex){
            Logger.getLogger(UserRedisImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
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
