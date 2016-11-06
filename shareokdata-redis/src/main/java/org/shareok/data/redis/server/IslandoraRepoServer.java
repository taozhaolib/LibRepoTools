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
public class IslandoraRepoServer extends RepoServer {
    private String islandoraUploadPath;
    private String drupalPath;
    private String tempFilePath;

    public String getIslandoraUploadPath() {
        return islandoraUploadPath;
    }

    public String getDrupalPath() {
        return drupalPath;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setIslandoraUploadPath(String islandoraUploadPath) {
        this.islandoraUploadPath = islandoraUploadPath;
    }

    public void setDrupalPath(String drupalPath) {
        this.drupalPath = drupalPath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }
    
}
