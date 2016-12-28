/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

/**
 *
 * @author Tao Zhao
 */
public interface RedisConfig {
    public void updateConfig(String configInfoType, String value);
    public boolean getRegistrationConfig();
}
