/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceApiHandler {
    public boolean isAuthorizedUser();
    public Map<String, Object> getUserInfoByToken();
    public String getTokenFromServer();
    public int getItemCountByCollectionHandler(String handler);
    public int getItemCountByCollectionId(String id);
    public String getItemsInfoByCollectionId(String id);
    public String getItemsInfoByCollectionHandler(String handler);
    public String[] getItemIdsByCollectionId(String id);
    public String[] getItemIdsByCollectionHandler(String handler);
    public String getObjectInfoByHandler(String handler);
    public String getObjectIdByHandler(String handler);
    public List<Map<String, Object>> getItemMetadataById(String id);
    
    public void updateItemMetadata(String id, List<Map<String, String>> data);
}
