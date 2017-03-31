/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.util.List;
import java.util.Map;
import org.shareok.data.datahandlers.JobHandler;
import org.shareok.data.redis.job.DspaceApiJob;
import org.shareok.data.redis.job.RedisJob;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceApiHandler extends JobHandler {
    public boolean isAuthorizedUser();
    
    public Map<String, Object> getUserInfoByToken(); 
    public Map<String, Object> createEmptyItem(String collectionId);
    public Map<String, String> getMetadataFromXmlFiles(String[] paths);
    public Map<String, String> addItemBitstream(String id, String filePath, String fileName, String description);
    
    public int getItemCountByCollectionHandler(String handle);
    public int getItemCountByCollectionHandler(String handle, String dspaceApiUrl);
    public int getItemCountByCollectionId(String id);
    public int getItemCountByCollectionId(String id, String dspaceApiUrl);
    
    public String getTokenFromServer();
    public String getItemsInfoByCollectionId(String id);
    public String getItemsInfoByCollectionHandler(String handle);
    public String getObjectInfoByHandler(String handle);
    public String getObjectInfoByHandler(String handle, String dspaceApiUrl);
    public String getObjectIdByHandler(String handle);    
    public String getObjectIdByHandler(String handle, String dspaceApiUrl);
    public String addItemMetadata(String id, String data);
    
    public String[] getItemIdsByCollectionId(String id);
    public String[] getItemIdsByCollectionId(String id, String dspaceApiUrl);
    public String[] getItemIdsByCollectionHandler(String handle);
    public String[] getItemIdsByCollectionHandler(String handle, String dspaceApiUrl);
    
    public Map<String, String> getItemDoisByCollectionHandler(String handle, String dspaceApiUrl);
    
    public String[] getMetadataValuesByKey(String itemId, String key); 
    public String[] getMetadataValuesByKey(String itemId, String key, String dspaceApiUrl);
    
    public List<Map<String, Object>> getItemMetadataById(String id);
    public List<Map<String, Object>> getItemMetadataById(String id, String dspaceApiUrl);
            
    public void updateItemMetadata(String id, List<Map<String, String>> data);
    public void deleteItemById(String id);
    public void deleteItemsByCollectionId(String id);
    
    public boolean checkDuplicatesByDoi(String doi, String collectionHandle, String dspaceApiUrl);
    
    @Override
    public void setJob(RedisJob job);
    @Override
    public DspaceApiJob getJob();
    
    public Map<String, List<String>> loadItemsFromSafPackage();
    public Map<String, List<String>> loadItemsFromSafPackage(String safPath, String collectionHandle, String dspaceApiUrl);
}
