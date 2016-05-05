/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

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
    public void setHandler(DspaceSshHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public String sshImportData(){
        return handler.importDspace();
    }
}
