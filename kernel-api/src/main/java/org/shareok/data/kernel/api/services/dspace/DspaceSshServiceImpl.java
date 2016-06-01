/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.shareok.data.config.DataHandler;
import org.shareok.data.dspacemanager.DspaceSshDataUtil;
import org.shareok.data.dspacemanager.DspaceSshHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */
@Service
public class DspaceSshServiceImpl implements DspaceSshService {
    private DspaceSshHandler handler;

    public DspaceSshHandler getHandler() {
        return handler;
    }

    @Override
    @Autowired
    public void setHandler(DataHandler handler) {
        this.handler = (DspaceSshHandler)handler;
        if(null == this.handler.getSshExec()){
            this.handler.setSshExec(DspaceSshDataUtil.getSshExecForDspace());
        }
    }
    
    @Override
    public String sshImportData(){
        return handler.importDspace();
    }
    
    @Override
    public String uploadSafDspace(){
        return handler.uploadSafDspace();
    }

    @Override
    public String executeTask(String jobType) {
        if(jobType.equals("ssh-import")){
            return handler.importDspace();
        }
        else if(jobType.equals("ssh-upload")){
            return handler.uploadSafDspace();
        }
        else if(jobType.equals("ssh-import-uloaded")){
            return handler.importUploadedSafDspace();
        }
        else{
            return null;
        }
    }
    
}
