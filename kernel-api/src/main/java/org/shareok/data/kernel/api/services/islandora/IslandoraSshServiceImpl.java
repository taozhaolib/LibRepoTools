/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.islandora;

import org.shareok.data.config.DataHandler;
import org.shareok.data.islandoramanager.IslandoraSshDataUtil;
import org.shareok.data.islandoramanager.IslandoraSshHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */
@Service
public class IslandoraSshServiceImpl implements IslandoraSshService {
    
    private IslandoraSshHandler handler; 

    @Override
    public String importIslandora() {
        return handler.importIslandora();
    }

    @Override
    @Autowired   
    @Qualifier("islandoraSshHandler")
    public void setHandler(DataHandler handler) {
        this.handler = (IslandoraSshHandler)handler;
        if(null == this.handler.getSshExec()){
            this.handler.setSshExec(IslandoraSshDataUtil.getSshExecForIslandora());
        }
    }

    @Override
    public String executeTask(String jobType) {
        switch (jobType) {
            case "ssh-import-islandora":
                return handler.importIslandora();
            default:
                return null;
        }
    }

    @Override
    public void setUserId(long userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
