/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Stores, updates configuration information.
 * 
 * @author Tao Zhao
 */
public class RedisConfigImpl implements RedisConfig {
    
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class);
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void updateConfig(String configInfoType, String value) {
        try{
            BoundHashOperations<String, String, String> configOps = redisTemplate.boundHashOps(RedisUtil.getConfigQueryKey());
            configOps.put(configInfoType, value);
        }
        catch(Exception ex){
            logger.error("Cannot update config info @ " + configInfoType + " with value = " + value, ex);
        }
    }

    @Override
    public boolean getRegistrationConfig() {
        String allowRegistration;
        try{
            BoundHashOperations<String, String, String> configOps = redisTemplate.boundHashOps(RedisUtil.getConfigQueryKey());
            if(configOps.hasKey("registrationConfig")){
                allowRegistration = (String)configOps.get("registrationConfig");
            }
            else{
                allowRegistration = "false";
                configOps.put("registrationConfig", "false");
            }
            return Boolean.valueOf(allowRegistration);
        }
        catch(Exception ex){
            logger.error("Cannot get allow registration info ", ex);
            return false;
        }
    }
}
