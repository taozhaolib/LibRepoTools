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

/**
 *
 * @author Tao Zhao
 */
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
    public List<RepoServer> getServerObjList(Collection<String> serverIds){
        return serverDao.getServerObjList(serverIds);
    }
    
    @Override
    public List<RepoServer> loadRepoServerListByRepoType(List<RepoServer> serverList){
        return serverDao.loadRepoServerListByRepoType(serverList);
    }
}
