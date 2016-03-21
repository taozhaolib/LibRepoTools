/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.user;

import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.UserRedis;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class RedisUserServiceImpl implements RedisUserService{
    
    @Autowired
    private UserRedis userRedis;
    
    @Override
    public RedisUser addUser(RedisUser user){
        return userRedis.addUser(user);
    }
    
    @Override
    public RedisUser updateUser(RedisUser user){
        return userRedis.updateUser(user);
    }
    //public RedisUser findUserByUserId(long userId);
    
    @Override
    public RedisUser findUserByUserEmail(String email){
        return userRedis.findUserByUserEmail(email);
    }
    
    @Override
    public RedisUser findAuthenticatedUser(String email, String sessionKey){
        return userRedis.findAuthenticatedUser(email, sessionKey);
    }
    //public void deleteUserByUserId(long userId);
    
    @Override
    public void deactivateUserByUserId(long userId){
        userRedis.deactivateUserByUserId(userId);
    }
    
    @Override
    public RedisUser getNewUser(){
        return userRedis.getNewUser();
    }
}
