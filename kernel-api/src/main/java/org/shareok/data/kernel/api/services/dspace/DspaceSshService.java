/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.shareok.data.dspacemanager.DspaceSshHandler;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceSshService {
    public void sshImportData();
    public void setHandler(DspaceSshHandler handler);
}
