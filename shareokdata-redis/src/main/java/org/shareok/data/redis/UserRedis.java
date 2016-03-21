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
public interface UserRedis {
    public RedisUser getNewUser();
    public RedisUser addUser(RedisUser user);
    public RedisUser updateUser(RedisUser user);
    public RedisUser findUserByUserId(long userId);
    public RedisUser findUserByUserEmail(String email);
    public RedisUser findAuthenticatedUser(String email, String sessionKey);
    public void deleteUserByUserId(long userId);
    public void deactivateUserByUserId(long userId);
}
