/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

import java.util.Map;
import org.shareok.data.config.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class RepoServerDaoHelper {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RepoServerDaoImpl.class);
    
    private Map<String, RepoTypeServerDao> serverDaos;

    public Map<String, RepoTypeServerDao> getServerDaos() {
        return serverDaos;
    }
    
    @Autowired
    public void setServerDaos(Map<String, RepoTypeServerDao> serverDaos) {
        this.serverDaos = serverDaos;
    }
    
    public RepoTypeServerDao getRepoServerDaoByRepoType(int repoType){
        try{
            String repoServerName = DataUtil.REPO_TYPES[repoType];
            return (RepoTypeServerDao)serverDaos.get(repoServerName+"RepoServerDaoImpl");        
        }
        catch(Exception ex){
            logger.error("Cannot get RepoServerDao instance "+ex.getMessage());
        }
        return null;
    }
}
