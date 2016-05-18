/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.user;

import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.UserDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class RedisUserServiceImpl implements RedisUserService{
    
    @Autowired
    private UserDao userDao;
    
    @Override
    public RedisUser addUser(RedisUser user){
        return userDao.addUser(user);
    }
    
    @Override
    public RedisUser updateUser(RedisUser user){
        return userDao.updateUser(user);
    }
    //public RedisUser findUserByUserId(long userId);
    
    @Override
    public RedisUser findUserByUserEmail(String email){
        return userDao.findUserByUserEmail(email);
    }
    
    @Override
    public RedisUser findAuthenticatedUser(String email, String sessionKey){
        return userDao.findAuthenticatedUser(email, sessionKey);
    }
    //public void deleteUserByUserId(long userId);
    
    @Override
    public void deactivateUserByUserId(long userId){
        userDao.deactivateUserByUserId(userId);
    }
    
    @Override
    public RedisUser getNewUser(){
        return RedisUtil.getUserInstance();
    }
    
    @Override
    public void invalidateUserSessionIdByEmail(String email){
        userDao.invalidateUserSessionIdByEmail(email);
    }
}
