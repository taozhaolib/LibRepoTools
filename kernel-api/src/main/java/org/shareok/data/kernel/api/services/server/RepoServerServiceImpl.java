/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.server;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.shareok.data.redis.server.RepoServer;
import org.shareok.data.redis.server.RepoServerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */
@Service
public class RepoServerServiceImpl implements RepoServerService{
    
    private RepoServerDao serverDao;

    public RepoServerDao getServerDao() {
        return serverDao;
    }

    @Autowired
    public void setServerDao(RepoServerDao serverDao) {
        this.serverDao = serverDao;
    }
    
    @Override
    public Map<String, String> getServerNameIdList(){
        return serverDao.getServerNameIdList();
    }
    
    @Override
    public RepoServer findServerById(int serverId){
        return serverDao.findServerById(serverId);
    }
    
    @Override
    public RepoServer findServerByName(String serverName){
        return serverDao.findServerByName(serverName);
    }
    
    @Override
    public List<RepoServer> getServerObjList(Collection<String> serverIds){
        return serverDao.getServerObjList(serverIds);
    }

    @Override
    public RepoServer updateServer(RepoServer server){
        return serverDao.updateServer(server);
    }
    
    @Override
    public RepoServer addServer(RepoServer server){
        return serverDao.addServer(server);
    }

    @Override
    public int findServerIdByName(String serverName) {
        return serverDao.findServerIdByName(serverName);
    }

    @Override
    public String[] getRepoTypeServerFields(int repoType) {
        return serverDao.getRepoTypeServerFields(repoType);
    }

    @Override
    public void updateRepoTypeServerFieldInfo(Map<String, String> repoTypeServerFieldInfo, RepoServer server) {
        serverDao.updateRepoTypeServerFieldInfo(repoTypeServerFieldInfo, server);
    }
}
