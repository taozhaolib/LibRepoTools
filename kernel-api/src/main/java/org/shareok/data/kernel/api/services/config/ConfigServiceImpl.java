/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.config;

import org.shareok.data.redis.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class ConfigServiceImpl implements ConfigService {

    private RedisConfig redisConfig;
    
    @Autowired
    public void setJobService(RedisConfig redisConfig) {
        this.redisConfig = (RedisConfig)redisConfig;
    }
    
    @Override
    public void updateRegistrationConfig(boolean allowRegistration) {
        redisConfig.updateConfig("registrationConfig", String.valueOf(allowRegistration));
    }

    @Override
    public boolean getRegistrationConfig() {
        return redisConfig.getRegistrationConfig();
    }
    
}
