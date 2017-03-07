/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.exceptions.NonExistingServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;

/**
 *
 * @author Tao Zhao
 */
public class RepoServerDaoImpl implements RepoServerDao {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RepoServerDaoImpl.class);
    
    @Autowired
    private RepoServerDaoHelper serverDaoHelper;
    
    @Autowired
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public RepoServer addServer(RepoServer server) {
        int serverIdCount = -1;
        
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            
            RedisAtomicInteger serverIdIndex = new RedisAtomicInteger(ShareokdataManager.getRedisServerQueryPrefix(), redisTemplate.getConnectionFactory());
            
            serverIdCount = serverIdIndex.incrementAndGet();
            final String serverId = String.valueOf(serverIdCount);
            final String serverName = server.getServerName();
            final String portStr = String.valueOf(server.getPort());
            final String proxyPortStr = String.valueOf(server.getProxyPort());
            final String repoTypeStr = String.valueOf(server.getRepoType());
            final String timeoutStr = String.valueOf(server.getTimeout());
            final String host = server.getHost();
            final String proxyHost = server.getProxyHost();
            final String userName = server.getUserName();
            final String proxyUserName = server.getProxyUserName();
            final String password = server.getPassword();
            final String proxyPassword = server.getProxyPassword();
            final String passphrase = server.getPassPhrase();
            final String rsaKey = server.getRsaKey();
            final String address = server.getAddress();
            
            RepoServer existingServer = findServerByName(serverName);
            if(null != existingServer){
                return existingServer;
            }
            
            List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.boundHashOps("server:"+serverId);
                    operations.opsForHash().put("server:"+serverId, "serverId", serverId);
                    operations.opsForHash().put("server:"+serverId, "serverName", serverName);
                    operations.opsForHash().put("server:"+serverId, "port", portStr);
                    operations.opsForHash().put("server:"+serverId, "proxyPort", proxyPortStr);
                    operations.opsForHash().put("server:"+serverId, "timeout", timeoutStr);
                    operations.opsForHash().put("server:"+serverId, "host", host);
                    operations.opsForHash().put("server:"+serverId, "proxyHost", proxyHost);
                    operations.opsForHash().put("server:"+serverId, "userName", userName);
                    operations.opsForHash().put("server:"+serverId, "proxyUserName", proxyUserName);
                    operations.opsForHash().put("server:"+serverId, "password", password);
                    operations.opsForHash().put("server:"+serverId, "host", host);
                    operations.opsForHash().put("server:"+serverId, "proxyPassword", proxyPassword);
                    operations.opsForHash().put("server:"+serverId, "passphrase", passphrase);
                    operations.opsForHash().put("server:"+serverId, "rsaKey", rsaKey);
                    operations.opsForHash().put("server:"+serverId, "repoType", repoTypeStr);
                    operations.opsForHash().put("server:"+serverId, "address", address);
                    
                    operations.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable());
                    operations.opsForHash().put(ShareokdataManager.getRedisServerNameIdMatchingTable(), serverName, serverId);
                    
                    List<Object> serverList= operations.exec();
                    if(!serverList.get(0).equals(true)){
                        operations.discard();
                    }
                    return serverList;
                }
            });
            server.setServerId(serverIdCount);
            return server;
        }
        catch(Exception ex){
            logger.error("Cannot create a new server.", ex);
            return null;
        }
    }
    
    @Override
    public RepoServer addServer(int port, int proxyPort, int timeout, int repoType, final String serverName, final String host, final String proxyHost, final String userName, 
            final String proxyUserName, final String password, final String proxyPassword, final String passphrase, final String rsaKey, final String address) {
        
        RepoServer server = new RepoServer();
        server.setPort(port);
        server.setProxyPort(proxyPort);
        server.setTimeout(timeout);
        server.setServerName(serverName);
        server.setHost(host);
        server.setProxyHost(proxyHost);
        server.setUserName(userName);
        server.setProxyUserName(proxyUserName);
        server.setPassword(password);
        server.setProxyPassword(proxyPassword);
        server.setPassPhrase(passphrase);
        server.setRsaKey(rsaKey);
        server.setRepoType(repoType);
        server.setAddress(address);
        
        return addServer(server);
    }
    
//    @Override
//    public DspaceServer addDspaceServer(DspaceServer ds){
//
//        RepoServer rs = addServer(ds);
//        if(null != rs){
//            addDspaceServerOnly(ds.getServerId(), ds.getDspaceDirectory(), ds.getUploadDst(), ds.getDspaceUser(), ds.getCollectionId());
//            ds.setHost(rs.getHost());
//            ds.setServerId(rs.getServerId());
//            ds.setPassPhrase(rs.getPassPhrase());
//            ds.setPassword(rs.getPassword());
//            ds.setPort(rs.getPort());
//            ds.setProxyHost(rs.getProxyHost());
//            ds.setProxyPassword(rs.getProxyPassword());
//            ds.setProxyPort(rs.getProxyPort());
//            ds.setProxyUserName(rs.getProxyUserName());
//            ds.setRepoType(rs.getRepoType());
//            ds.setRsaKey(rs.getRsaKey());
//            ds.setServerName(rs.getServerName());
//            ds.setTimeout(rs.getTimeout());
//            ds.setUserName(rs.getUserName());
//            return ds;
//        }
//        else{
//            return null;
//        }
//    }
    
//    @Override
//    public void addDspaceServerOnly(int serverId, String dspaceDirectory, String uploadDst, String dspaceUser, String collectionId){
//        try{
//            BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisDspaceServerTablePrefix()+String.valueOf(serverId));
//            if(null != serverOps){
//                serverOps.put("dspaceDirectory", dspaceDirectory);
//                serverOps.put("uploadDst", uploadDst);
//                serverOps.put("dspaceUser", dspaceUser);
//                serverOps.put("collectionId", collectionId);
//            }
//        }
//        catch(Exception ex){
//            logger.error("Cannot add DSpace server information", ex);
//        }
//    }
    
//    @Override
//    public DspaceServer updateDspaceServer(DspaceServer ds){
//        updateServer(ds);
//        addDspaceServerOnly(ds.getServerId(), ds.getDspaceDirectory(), ds.getUploadDst(), ds.getDspaceUser(), ds.getCollectionId());
//        return ds;
//    }

    @Override
    public RepoServer updateServer(int serverId, String infoType, String value) {
        RepoServer server = null;
        try{
            if(infoType.equals("serverName")){
                RepoServer existingServer = findServerById(serverId);
                if(null == existingServer){
                    throw new NonExistingServerException("The server to be updated does not exist!");
                }
                String oldServerName = existingServer.getServerName(); 
                if(!oldServerName.equals(value)){
                    redisTemplate.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable()).delete(ShareokdataManager.getRedisServerNameIdMatchingTable(), oldServerName);
                    redisTemplate.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable()).put(value, String.valueOf(serverId));
                }
            }
            server = findServerById(serverId);
            BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(RedisUtil.getServerQueryKey(serverId));            
            serverOps.put(infoType, value);
        }
        catch(Exception ex){
            logger.error("Cannot update server info @ " + infoType + " with value = " + value, ex);
        }
        return server;
    }
    
    @Override
    public RepoServer updateServer(RepoServer server){
        
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            
            final String serverId = String.valueOf(server.getServerId());
            final String serverName = server.getServerName();
            
            RepoServer existingServer = findServerById(server.getServerId());
            if(null == existingServer){
                throw new NonExistingServerException("The server to be updated does not exist!");
            }
            
            final String oldServerName = existingServer.getServerName();            
            final String portStr = String.valueOf(server.getPort());
            final String proxyPortStr = String.valueOf(server.getProxyPort());
            final String repoTypeStr = String.valueOf(server.getRepoType());
            final String timeoutStr = String.valueOf(server.getTimeout());
            final String host = server.getHost();
            final String proxyHost = server.getProxyHost();
            final String userName = server.getUserName();
            final String proxyUserName = server.getProxyUserName();
            final String password = server.getPassword();
            final String proxyPassword = server.getProxyPassword();
            final String passPhrase = server.getPassPhrase();
            final String rsaKey = server.getRsaKey();
            final String address = server.getAddress();
            
            List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.boundHashOps("server:"+serverId);
//                    operations.opsForHash().put("server:"+serverId, "serverId", serverId);
                    operations.opsForHash().put("server:"+serverId, "serverName", serverName);
                    operations.opsForHash().put("server:"+serverId, "port", portStr);
                    operations.opsForHash().put("server:"+serverId, "proxyPort", proxyPortStr);
                    operations.opsForHash().put("server:"+serverId, "timeout", timeoutStr);
                    operations.opsForHash().put("server:"+serverId, "host", host);
                    operations.opsForHash().put("server:"+serverId, "proxyHost", proxyHost);
                    operations.opsForHash().put("server:"+serverId, "userName", userName);
                    operations.opsForHash().put("server:"+serverId, "proxyUserName", proxyUserName);
                    operations.opsForHash().put("server:"+serverId, "password", password);
                    operations.opsForHash().put("server:"+serverId, "host", host);
                    operations.opsForHash().put("server:"+serverId, "proxyPassword", proxyPassword);
                    operations.opsForHash().put("server:"+serverId, "passPhrase", passPhrase);
                    operations.opsForHash().put("server:"+serverId, "rsaKey", rsaKey);
                    operations.opsForHash().put("server:"+serverId, "repoType", repoTypeStr);
                    operations.opsForHash().put("server:"+serverId, "address", address);
                    
                    operations.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable());
                    if(!oldServerName.equals(serverName)){
                        operations.opsForHash().delete(ShareokdataManager.getRedisServerNameIdMatchingTable(), oldServerName);
                    }
                    operations.opsForHash().put(ShareokdataManager.getRedisServerNameIdMatchingTable(), serverName, serverId);
                    
                    List<Object> serverList= operations.exec();
                    if(serverList.get(0).equals("null")){
                        operations.discard();
                    }
                    return serverList;
                }
            });
        }
        catch(Exception ex){
            logger.error("Cannot update the server information", ex);
        }
        return server;
    }

    @Override
    public RepoServer findServerById(int serverId) {
        try{
            BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(RedisUtil.getServerQueryKey(serverId));
            if(null != serverOps){
                RepoServer server = RedisUtil.getServerInstance();
                int repoType = Integer.parseInt(serverOps.get("repoType"));
                server.setServerId(serverId);
                server.setServerName(serverOps.get("serverName"));
                server.setPort(Integer.parseInt(serverOps.get("port")));
                server.setProxyPort(Integer.parseInt(serverOps.get("proxyPort")));
                server.setRepoType(repoType);
                server.setTimeout(Integer.parseInt(serverOps.get("timeout")));
                server.setHost(serverOps.get("host"));
                server.setProxyHost(serverOps.get("proxyHost"));
                server.setUserName(serverOps.get("userName"));
                server.setProxyUserName(serverOps.get("proxyUserName"));
                server.setPassword(serverOps.get("password"));
                server.setProxyPassword(serverOps.get("proxyPassword"));
                server.setPassPhrase(serverOps.get("passPhrase"));
                server.setRsaKey(serverOps.get("rsaKey"));
                server.setAddress(serverOps.get("address"));
                server = serverDaoHelper.getRepoServerDaoByRepoType(repoType).loadServerParametersByRepoType(server, serverOps);
                return server;
            }
            else{
                return null;
            }
        }
        catch(Exception ex){
            logger.error("Cannot find the server information by server ID "+serverId, ex);
        }
        return null;
    }

    @Override
    public RepoServer findServerByName(String serverName) {
        BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable());
        if(null != serverOps){
            String id = serverOps.get(serverName);
            if(null != id && !id.equals("")){                
                return findServerById(Integer.parseInt(id));
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
    public int findServerIdByName(String serverName){
        BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable());
        if(null != serverOps){
            String id = serverOps.get(serverName);
            if(null != id && !id.equals("")){                
                return Integer.parseInt(id);
            }            
            else{
                return -1;
            }
        }
        else{
            return -1;
        }
    }
    
    @Override
    public Map<String, String> getServerNameIdList(){
        
        Map<String, String> serverList = new HashMap<>();
        BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable());
        if(null != serverOps){
            Map map = serverOps.entries();
            Iterator it = map.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();               
                serverList.put((String)pair.getKey(), (String)pair.getValue());
                it.remove();
            }
        }
        return serverList;
    }

    @Override
    public List<RepoServer> getServerObjList(final Collection<String> serverIds){
        
        final List<RepoServer> serverList = new ArrayList<>();
        final ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
        
        try{
            List<Object> results = redisTemplate.executePipelined(
            new RedisCallback<Object>() {
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    for(String idStr : serverIds){
                        int id = Integer.parseInt(idStr);
                        BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(RedisUtil.getServerQueryKey(id));
                        if(null != serverOps){
                            RepoServer server = new RepoServer();
                            int repoType = Integer.parseInt(serverOps.get("repoType"));
                            server.setServerId(id);
                            server.setServerName(serverOps.get("serverName"));
                            server.setPort(Integer.parseInt(serverOps.get("port")));
                            server.setProxyPort(Integer.parseInt(serverOps.get("proxyPort")));
                            server.setTimeout(Integer.parseInt(serverOps.get("timeout")));
                            server.setHost(serverOps.get("host"));
                            server.setProxyHost(serverOps.get("proxyHost"));
                            server.setUserName(serverOps.get("userName"));
                            server.setProxyUserName(serverOps.get("proxyUserName"));
                            server.setPassword(serverOps.get("password"));
                            server.setProxyPassword(serverOps.get("proxyPassword"));
                            server.setPassPhrase(serverOps.get("passPhrase"));
                            server.setRsaKey(serverOps.get("rsaKey"));
                            server.setRepoType(repoType);
                            server.setAddress(serverOps.get("address"));                           
                            server = serverDaoHelper.getRepoServerDaoByRepoType(repoType).loadServerParametersByRepoType(server, serverOps);
                            serverList.add(server);
                        }
                    }
                    return null;
                }
            });

            return serverList;
        }
        catch(Exception ex){
            logger.error("Cannot get the server list from the server ID collection.", ex);
        }
        return null;
    }
    
    @Override
    public String[] getRepoTypeServerFields(int repoType){
        
        String[] fields = null;
        
        switch(repoType){
            case 1:
                fields = new String[]{"dspacePath","dspaceUploadPath", "prefix"};
                break;
            case 2:
                fields = new String[]{"islandoraUploadPath","drupalPath","tempFilePath"};
                break;
            default:
                break;
        }
        return fields;
    }
    
    public void updateRepoTypeServerFieldInfo(Map<String, String> repoTypeServerFieldInfo, RepoServer server){
        BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(RedisUtil.getServerQueryKey(server.getServerId()));
            if(null != serverOps){
                for(String key : repoTypeServerFieldInfo.keySet()){
                    serverOps.put(key, repoTypeServerFieldInfo.get(key));
                }
            }            
    }
    
//    @Override
//    public RepoServer loadRepoServerByRepoType(RepoServer server){
//        try{
//            int serverId = server.getServerId();
//            String repoType = DataUtil.REPO_TYPES[server.getRepoType()];
//            ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
//
//            if("dspace".equals(repoType)){            
//                BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisDspaceServerTablePrefix()+String.valueOf(serverId));
//                if(null != serverOps){
//                    if("dspace".equals(repoType)){
//                        DspaceServer ds = (DspaceServer) RedisUtil.getServerInstanceByRepoType(repoType, context);
//                        ds.setDspaceDirectory((String)serverOps.get("dspaceDirectory"));
//                        ds.setUploadDst((String)serverOps.get("uploadDst"));
//                        ds.setDspaceUser((String)serverOps.get("dspaceUser"));
//                        ds.setCollectionId((String)serverOps.get("collectionId"));
//                        ds.setHost(server.getHost());
//                        ds.setPassPhrase(server.getPassPhrase());
//                        ds.setPassword(server.getPassword());
//                        ds.setPort(server.getPort());
//                        ds.setProxyHost(server.getProxyHost());
//                        ds.setProxyPassword(server.getProxyPassword());
//                        ds.setProxyPort(server.getProxyPort());
//                        ds.setProxyUserName(server.getProxyUserName());
//                        ds.setRepoType(server.getRepoType());
//                        ds.setRsaKey(server.getRsaKey());
//                        ds.setServerId(server.getServerId());
//                        ds.setServerName(server.getServerName());
//                        ds.setTimeout(server.getTimeout());
//                        ds.setUserName(server.getUserName());
//                        
//                        
//                        return ds;
//                    }
//                }
//            }
//        }
//        catch(Exception ex){
//            logger.error("Cannot load the server information for repo type = " + DataUtil.REPO_TYPES[server.getRepoType()] + " with server ID = " + String.valueOf(server.getServerId()) , ex);
//        }
//            
//        return null;
//    }
    
//    @Override
//    public List<RepoServer> loadRepoServerListByRepoType(final List<RepoServer> serverList){
//        
//        final List<RepoServer> repoServerList = new ArrayList<>();
//        final ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
//        
//        try{            
//            List<RepoServer> results = RedisUtil.getTemplateInstance(context).executePipelined(
//            new RedisCallback<Object>() {
//                public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                    for(RepoServer server : serverList){                        
//                        repoServerList.add(loadRepoServerByRepoType(server));                        
//                    }
//                    return null;
//                }
//            });
//
//            return repoServerList;
//        }
//        catch(Exception ex){
//            logger.error("Cannot get the list of the servers based on the server repo types.", ex);
//        }
//        return null;
//    }
}
