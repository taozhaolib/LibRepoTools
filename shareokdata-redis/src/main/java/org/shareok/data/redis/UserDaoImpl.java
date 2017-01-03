/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
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
public class UserDaoImpl implements UserDao {
    
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class);
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;

    // inject the template as ListOperations
    @Resource(name="redisTemplate")
    private ListOperations<String, String> listOps;
    
    @Override
    @Transactional
    public RedisUser addUser(final RedisUser user){
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            RedisUser existedUser = findUserByUserEmail(user.getEmail());
            if(null != existedUser){
                return existedUser;
            }
            
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
                    operations.opsForHash().put("user:"+uid, "sessionKey", (null != user.getSessionKey() ? user.getSessionKey() : ""));
                    operations.opsForHash().put("user:"+uid, "startTime", (null != user.getStartTime() ? ShareokdataManager.getSimpleDateFormat().format(user.getStartTime()) : (ShareokdataManager.getSimpleDateFormat().format(new Date()))));
                    
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
            logger.error("Cannot add new user", ex);
        }
        return user;
    }
    
    @Override
    public RedisUser updateUser(final RedisUser user){
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
                    operations.put(userKey, "sessionKey", (null != user.getSessionKey() ? user.getSessionKey() : ""));

                    if(!oldUserEmail.equals(user.getEmail())){
                        operations.delete("users", oldUserEmail);
                        operations.put("users", user.getEmail(), uid);
                    }
                    return null;
                }
            });
        }
        catch(Exception ex){
            logger.error("Cannot update a user", ex);
        }
        return user;
    }
    
    @Override
    public RedisUser findUserByUserId(long userId){
        BoundHashOperations<String, String, String> userOps = redisTemplate.boundHashOps(RedisUtil.getUserQueryKey(userId));
        if(null != userOps){
            ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
            String email = userOps.get("email");
            if(null != email && !email.equals("")){
                RedisUser user = (RedisUser) context.getBean("user");
                user.setEmail(userOps.get("email"));
                user.setUserName(userOps.get("userName"));
                user.setUserId(Long.parseLong(userOps.get("userId")));
                user.setPassword(userOps.get("password"));
                user.setSessionKey(userOps.get("sessionKey"));
                return user;
            }
        }
        return null;
    }
    
    @Override
    public RedisUser findUserByUserEmail(String email){
        BoundHashOperations<String, String, String> userOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisUserNameIdMatchingTable());
        if(null != userOps){
            String id = userOps.get(email);
            if(null != id && !id.equals("")){
                long userId = Long.valueOf(userOps.get(email));
                return findUserByUserId(userId);
            }            
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }
    
    @Override
    public RedisUser findAuthenticatedUser(String userName, String sessionKey){
        RedisUser user = findUserByUserEmail(userName);
        if(null != user && sessionKey.equals(user.getSessionKey())){
            return user;
        }
        return null;
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
            logger.error("Cannot delete a user with ID "+userId, ex);
        }
    }

    @Override
    public void deactivateUserByUserId(long userId){
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            final String key = RedisUtil.getUserQueryKey(userId);
            redisTemplate.opsForHash().put(key, "isActive", String.valueOf(false));
        }
        catch(Exception ex){
            logger.error("Cannot deactivate a user", ex);
        }
    }
    
    @Transactional
    public void test2(){
        RedisTemplate<String, Object> redis = new RedisTemplate<>();
        redis.setConnectionFactory(connectionFactory);
//        redis.setKeySerializer(ApplicationConfig.StringSerializer.INSTANCE);
//        redis.setValueSerializer(new JacksonJsonRedisSerializer<User>(UserDaoImpl.class));
        redis.afterPropertiesSet();
        redis.setEnableTransactionSupport(true);//奇怪的是一定要再显示开启redistemplate的事务支持
        redis.multi();  
        redis.boundValueOps("somevkey").increment(1);  
        redis.boundZSetOps("somezkey").add("zvalue", 11);  
        redis.exec(); 
        redis.setEnableTransactionSupport(false);
    }

    @Override
    public void invalidateUserSessionIdByEmail(String email){
        RedisUser user = findUserByUserEmail(email);
        user.setSessionKey("");
        updateUser(user);
    }
}
