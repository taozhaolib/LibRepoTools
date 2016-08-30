/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.util.List;
import java.util.Map;
import org.shareok.data.config.DataHandler;
import org.shareok.data.redis.job.DspaceApiJob;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceApiHandler extends DataHandler {
    public boolean isAuthorizedUser();
    
    public Map<String, Object> getUserInfoByToken(); 
    public Map<String, Object> createEmptyItem(String collectionId);
    public Map<String, String> getMetadataFromXmlFiles(String[] paths);
    public Map<String, String> addItemBitstream(String id, String filePath, String fileName, String description);
    
    public int getItemCountByCollectionHandler(String handle);
    public int getItemCountByCollectionId(String id);
    
    public String getTokenFromServer();
    public String getItemsInfoByCollectionId(String id);
    public String getItemsInfoByCollectionHandler(String handle);
    public String getObjectInfoByHandler(String handle);
    public String getObjectIdByHandler(String handle);    
    public String addItemMetadata(String id, String data);
    
    public String[] getItemIdsByCollectionId(String id);
    public String[] getItemIdsByCollectionHandler(String handle);
    public String[] getMetadataValuesByKey(String itemId, String key);    
    
    public List<Map<String, Object>> getItemMetadataById(String id);
            
    public void updateItemMetadata(String id, List<Map<String, String>> data);
    public void deleteItemById(String id);
    public void deleteItemsByCollectionId(String id);
    
    public void setJob(DspaceApiJob job);
    public DspaceApiJob getJob();
    public Map<String, List<String>> loadItemsFromSafPackage();
}
