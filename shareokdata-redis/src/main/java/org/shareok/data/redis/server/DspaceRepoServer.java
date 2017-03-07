/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

/**
 *
 * @author Tao Zhao
 */
public class DspaceRepoServer extends RepoServer {
    private String prefix;
    private String dspacePath;
    private String dspaceUploadPath;

    public String getPrefix() {
        return prefix;
    }

    public String getDspacePath() {
        return dspacePath;
    }

    public String getDspaceUploadPath() {
        return dspaceUploadPath;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setDspacePath(String dspacePath) {
        this.dspacePath = dspacePath;
    }

    public void setDspaceUploadPath(String dspaceUploadPath) {
        this.dspaceUploadPath = dspaceUploadPath;
    }
    
}
