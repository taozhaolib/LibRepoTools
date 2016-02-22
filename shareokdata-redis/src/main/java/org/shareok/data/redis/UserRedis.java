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
    public User addUser(User user);
    public User updateUser(User user);
    public User findUserByUserId(long userId);
    public User deleteUserByUserId(long userId);
}
