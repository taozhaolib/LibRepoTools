/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.shareok.data.redis.server.DspaceServer;
import org.shareok.data.redis.server.RepoServer;
import org.shareok.data.redis.server.RepoServerDao;
import org.shareok.data.redis.server.RepoServerDaoImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        RepoServerDaoImpl temp = (RepoServerDaoImpl) context.getBean("serverDaoImpl");
        DspaceServer ds = (DspaceServer)context.getBean("dspaceServer");
        ds.setCollectionId("123456789/85");
        ds.setDspaceDirectory("/srv/dspace");
        ds.setDspaceUser("tao");
        ds.setUploadDst("/home/tao");
        temp.addDspaceServerOnly(2, ds.getDspaceDirectory(), ds.getUploadDst(), ds.getDspaceUser(), ds.getCollectionId());
        //temp.updateServer(2, "repoType", "1");
//        temp.addDspaceServerOnly(1, "/srv/shareok/dspace", "/home/svc-libtools-dev", "tao.zhao.lib@gmail.com", "11244/33301");
//        List<String> serverList = new ArrayList<>();
//        serverList.add("1");
//        serverList.add("2");
//        List<RepoServer> list = temp.getServerObjList(serverList);
        
//        RedisUser user = impl.findUserByUserEmail("tao.zhao.admin@ou.edu");
//        System.out.println("role = "+String.valueOf(user.getRole()));
//        RepoServer server = RedisUtil.getServerInstance(context);
//        RepoServerDao serverDao = RedisUtil.getServerDao(context);
//        
//        Map ok = serverDao.getServerNameIdList();
        
//        server.setHost("dspace-5xtest.test.sok.ec2.internal");
//        server.setPassPhrase("xiaomi");
//        server.setPassword("");
//        server.setPort(22);
//        server.setProxyHost("52.7.166.104");
//        server.setProxyPassword("");
//        server.setProxyPort(22);
//        server.setProxyUserName("tao");
//        server.setRsaKey("/Users/zhao0677/.ssh/tao_20160506_id_rsa");
//        server.setUserName("tao");
//        server.setServerName("test.shareok.org");
        
//        server.setHost("dev.repository.ou.edu");
//        server.setPassPhrase("donotknow");
//        server.setPassword("");
//        server.setPort(22);
//        server.setProxyHost("12.34.56.789");
//        server.setProxyPassword("");
//        server.setProxyPort(22);
//        server.setProxyUserName("tao");
//        server.setRsaKey("/okok/key");
//        server.setUserName("tao");
//        server.setServerName("dev.repository.ou.edu");
//        
//        serverDao.addServer(server);
        
        
//        RedisUser user = impl.findUserByUserEmail("test@gmail.com");
//        RedisUser user = (RedisUser) context.getBean("user");
//        user.setUserName("admin-libtools@ou.edu");
//        user.setEmail("tao.zhao.admin@ou.edu");
//        user.setPassword("admin");
////        user.setSessionKey(RedisUtil.getRandomString());
//        user.setStartTime(new Date());
//        user.setRole(1);
//        impl.updateUser(user);
//        impl.addUser(user);
//        RedisUser user = impl.findUserByUserEmail("tao.zhao@ou.edu");
      //  impl.deactivateUserByUserId(user.getUserId());
//        JobDao jobDao = RedisUtil.getJobDao();
//        long jobId = jobDao.startJob(20, 1, 1, null);
//        RedisJob job = jobDao.findJobByJobId(jobId);
//        System.out.print(job.getWorker() + " *** \n");
//        List<RedisJob> jobs = jobDao.getJobListByUser(20);
        System.out.println("Redis template is working!");
    }
}
