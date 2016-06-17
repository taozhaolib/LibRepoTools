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
public class DspaceServer extends RepoServer {
    private String dspaceDirectory;
    private String uploadDst;
    private String dspaceUser;
    private String collectionId;

    public String getDspaceDirectory() {
        return dspaceDirectory;
    }

    public String getUploadDst() {
        return uploadDst;
    }

    public String getDspaceUser() {
        return dspaceUser;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setDspaceDirectory(String dspaceDirectory) {
        this.dspaceDirectory = dspaceDirectory;
    }

    public void setUploadDst(String uploadDst) {
        this.uploadDst = uploadDst;
    }

    public void setDspaceUser(String dspaceUser) {
        this.dspaceUser = dspaceUser;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    
}
