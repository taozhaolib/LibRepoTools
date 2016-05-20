/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

import java.util.List;
import java.util.Map;

/**
 *
 * private int timeout;
    
    private int port;
    private int proxyPort;
    private String host;    
    private String proxyHost;
    private String userName;
    private String proxyUserName;
    private String password;
    private String proxyPassword;
    private String passPhrase;    
    private String rsaKey;
 * @author Tao Zhao
 */
public interface RepoServerDao {
    public RepoServer addServer(RepoServer server);
    public RepoServer addServer(int port, int porxyPort, int timeout, String serverName, String host, String proxyHost, String userName, String proxyUserName, 
                                String password, String proxyPassword, String passphrase, String rsaKey);
    public RepoServer updateServer(int serverId, String infoType, String value);
    public RepoServer findServerById(int serverId);
    public RepoServer findServerByName(String serverName);
    public List<Map<String, String>> getServerNameIdList();
    //public List<String> getServerNameList();
}
