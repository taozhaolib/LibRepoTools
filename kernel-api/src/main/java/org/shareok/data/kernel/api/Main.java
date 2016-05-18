/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api;

import java.util.Date;
import org.shareok.data.kernel.api.services.dspace.DspaceSshServiceImpl;
import org.shareok.data.kernel.api.services.user.RedisUserServiceImpl;
import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.RedisUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
//        RedisUser user = new RedisUser();
//        user.setUserName("tao.zhao.test2@ou.edu");
//        user.setEmail("tao.zhao.test2@ou.edu");
//        user.setPassword("12345");
//        user.setSessionKey(RedisUtil.getRandomString());
//        user.setStartTime(new Date());
        DspaceSshServiceImpl impl = (DspaceSshServiceImpl) context.getBean("dspaceSshServiceImpl");
        impl.getHandler().setHost("172.28.128.7");
        impl.getHandler().setPort(22);        
        impl.getHandler().setUserName("vagrant");
        impl.getHandler().setPassword("vagrant");
        impl.getHandler().setUploadDst("/home/vagrant");
        impl.getHandler().setUploadFile("/Users/zhao0677/Projects/shareokdata/test-load.tar");
        impl.getHandler().setDspaceUser("tao.zhao@ou.edu");
        impl.getHandler().setDspaceDirectory("/srv/dspace");
        impl.getHandler().setCollectionId("123456789/2");
        impl.sshImportData();
//        impl.addUser(user);
    }
}
