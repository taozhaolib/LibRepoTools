/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

import java.util.Collection;
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
    public RepoServer addServer(int port, int porxyPort, int timeout, int repoType, String serverName, String host, String proxyHost, String userName, String proxyUserName, 
                                String password, String proxyPassword, String passphrase, String rsaKey);
    public DspaceServer addDspaceServer(DspaceServer ds);
    public void addDspaceServerOnly(int serverId, String dspaceDirectory, String uploadDst, String dspaceUser, String collectionId);
    public DspaceServer updateDspaceServer(DspaceServer ds);
    public RepoServer updateServer(int serverId, String infoType, String value);
    public RepoServer updateServer(RepoServer server);
    public RepoServer findServerById(int serverId);
    public RepoServer findServerByName(String serverName);
    public Map<String, String> getServerNameIdList();
    public List<RepoServer> getServerObjList(Collection<String> serverIds);
    public RepoServer loadRepoServerByRepoType(RepoServer server);
    public List<RepoServer> loadRepoServerListByRepoType(List<RepoServer> serverList);
}
