/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

import java.util.List;
import java.util.Map;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
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
    private JedisConnectionFactory connectionFactory;
            
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public RepoServer addServer(RepoServer server) {
        int serverIdCount = -1;
        
        try{
            redisTemplate.setConnectionFactory(connectionFactory);
            
            RedisAtomicInteger serverIdIndex = new RedisAtomicInteger(ShareokdataManager.getRedisGlobalServerIdSchema(), redisTemplate.getConnectionFactory());
            
            serverIdCount = serverIdIndex.incrementAndGet();
            final String serverId = String.valueOf(serverIdCount);
            final String serverName = server.getServerName();
            final String portStr = String.valueOf(server.getPort());
            final String proxyPortStr = String.valueOf(server.getProxyPort());
            final String timeoutStr = String.valueOf(server.getTimeout());
            final String host = server.getHost();
            final String proxyHost = server.getProxyHost();
            final String userName = server.getUserName();
            final String proxyUserName = server.getProxyUserName();
            final String password = server.getPassword();
            final String proxyPassword = server.getProxyPassword();
            final String passphrase = server.getPassPhrase();
            final String rsaKey = server.getRsaKey();
            
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
                    
                    operations.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable());
                    operations.opsForHash().put(ShareokdataManager.getRedisServerNameIdMatchingTable(), serverName, serverId);
                    
                    List<Object> serverList= operations.exec();
                    if(!serverList.get(0).equals(true)){
                        operations.discard();
                    }
                    return serverList;
                }
            });
        }
        catch(Exception ex){
            logger.error("Cannot create a new server.", ex);
        }
        return server;
    }
    
    @Override
    public RepoServer addServer(int port, int proxyPort, int timeout, final String serverName, final String host, final String proxyHost, final String userName, 
            final String proxyUserName, final String password, final String proxyPassword, final String passphrase, final String rsaKey) {
        
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
        
        return addServer(server);
    }

    @Override
    public RepoServer updateServer(int serverId, String infoType, String value) {
        RepoServer server = null;
        try{
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
    public RepoServer findServerById(int serverId) {
        try{
            BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(RedisUtil.getServerQueryKey(serverId));
            if(null != serverOps){
                RepoServer server = RedisUtil.getServerInstance(null);
                server.setServerId(serverId);
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
    public List<Map<String, String>> getServerNameIdList(){
        BoundHashOperations<String, String, String> serverOps = redisTemplate.boundHashOps(ShareokdataManager.getRedisServerNameIdMatchingTable());
        if(null != serverOps){
            Map map = serverOps.entries();
        }
        return null;
    }

}
