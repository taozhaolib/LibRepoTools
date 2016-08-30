/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.job;

/**
 *
 * @author Tao Zhao
 * 
 *  Note: dspace username, password and token are not saved anywhere
 * 
 */
public class DspaceApiJob extends RedisJob {
    private String dspaceUserName;
    private String dspaceUserPw;
    private String token;
    private String communityId;
    private String subCommunityId;
    private String collectionId;
    private String itemId;
    private String bitstreamId;
    private String policyId;

    public String getDspaceUserName() {
        return dspaceUserName;
    }

    public String getDspaceUserPw() {
        return dspaceUserPw;
    }

    public String getToken() {
        return token;
    }

    public String getCommunityId() {
        return communityId;
    }

    public String getSubCommunityId() {
        return subCommunityId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getBitstreamId() {
        return bitstreamId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setDspaceUserName(String dspaceUserName) {
        this.dspaceUserName = dspaceUserName;
    }

    public void setDspaceUserPw(String dspaceUserPw) {
        this.dspaceUserPw = dspaceUserPw;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public void setSubCommunityId(String subCommunityId) {
        this.subCommunityId = subCommunityId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setBitstreamId(String bitstreamId) {
        this.bitstreamId = bitstreamId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }
}
