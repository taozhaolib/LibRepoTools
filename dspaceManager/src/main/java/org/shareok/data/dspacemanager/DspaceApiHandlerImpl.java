/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.shareok.data.config.DataUtil;
import org.shareok.data.dspacemanager.exceptions.EmptyDspaceCredentialInfoException;
import org.shareok.data.dspacemanager.exceptions.ErrorDspaceApiResponseException;
import org.shareok.data.htmlrequest.HttpRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class DspaceApiHandlerImpl implements DspaceApiHandler{
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DspaceApiHandlerImpl.class);
    
    private String token;
    private String dspaceUserName;
    private String dspacePassword;
    private String dspaceApiUrl;
    private HttpRequestHandler httpRequestHandler;

    public String getToken() {
        return token;
    }

    public String getDspaceUserName() {
        return dspaceUserName;
    }

    public String getDspacePassword() {
        return dspacePassword;
    }

    public String getDspaceApiUrl() {
        return dspaceApiUrl;
    }

    public HttpRequestHandler getHttpRequestHandler() {
        return httpRequestHandler;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDspaceUserName(String dspaceUserName) {
        this.dspaceUserName = dspaceUserName;
    }

    public void setDspacePassword(String dspacePassword) {
        this.dspacePassword = dspacePassword;
    }

    public void setDspaceApiUrl(String dspaceApiUrl) {
        this.dspaceApiUrl = dspaceApiUrl;
    }

    @Autowired
    public void setHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
        this.httpRequestHandler = httpRequestHandler;
    }
    
    @Override
    public String getTokenFromServer(){
        try{
            if(null == dspaceUserName || "".equals(dspaceUserName) || null == dspacePassword || "".equals(dspacePassword)){
                throw new EmptyDspaceCredentialInfoException("The DSpace username or DSpace password information is missing!");
            }
            Map<String, String> headerInfo = new HashMap<>();
            headerInfo.put("Content-Type", "application/json");            
            String response = httpRequestHandler.requestWithHeaderInfo(dspaceApiUrl+"/login", "POST", headerInfo, "{\"email\":\""+dspaceUserName+"\",\"password\":\""+dspacePassword+"\"}").toString();
            String[]responseInfo = response.split("\\n");
            if(null != responseInfo[0]){
                if(responseInfo[0].equals("200")){
                    token = responseInfo[1];
                    return token;
                }
                else{
                    throw new ErrorDspaceApiResponseException("Got response code "+responseInfo[0]);
                }
            }
            //return response;
        }
        catch(EmptyDspaceCredentialInfoException empEx){
            logger.error("Missing DSpace log in information", empEx);
        } catch (ErrorDspaceApiResponseException ex) {
            logger.error("Missing DSpace logging in information", ex);
        }
        return null;
    }
    
    @Override
    public boolean isAuthorizedUser(){
        Map<String, Object> userInfo = getUserInfoByToken();
        
        if(null != userInfo){
            Object auth = userInfo.get("authenticated");
            if(null != auth && (Boolean)auth == true){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getUserInfoByToken(){
        
        if(null == token || "".equals(token)){
            getTokenFromServer();
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        Map<String, String>header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        header.put("rest-dspace-token", token);
        
        try{
            String response = httpRequestHandler.requestWithHeaderInfo(dspaceApiUrl + "/status", "GET", header, null).toString();
            String[] userInfoResponseArr = response.split("\\n");
            if(null != userInfoResponseArr[0] && userInfoResponseArr[0].equals("200")){
                response = userInfoResponseArr[1];
            }
            else{
                throw new ErrorDspaceApiResponseException("Got the response code "+userInfoResponseArr[0]);
            }
            return DataUtil.getMapFromJson(response);
        }
        catch(ErrorDspaceApiResponseException ex){
            logger.error("Cannot get the user information by token!", ex);
        }
        return null;
    }
    
    @Override
    public String getItemsInfoByCollectionId(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemsInfoByCollectionHandler(String handler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getObjectIdByHandler(String handler) {
        if(null == token || "".equals(token)){
            token = getTokenFromServer();
        }
        Map map = DataUtil.getMapFromJson(getObjectInfoByHandler(handler));
        if(null != map.get("id")){
            return String.valueOf(map.get("id"));
        }
        return null;
    }
    
    @Override
    public String getObjectInfoByHandler(String handler){
        try{
            String objectInfo = httpRequestHandler.sendGet(dspaceApiUrl + "/handle/" + handler);
            String[] objectInfoArr = objectInfo.split("\\n");
            if(null != objectInfoArr[0] && objectInfoArr[0].equals("200")){
                return objectInfoArr[1];
            }
            else{
                throw new ErrorDspaceApiResponseException("Got the response code "+objectInfoArr[0]);
            }
        }
        catch(ErrorDspaceApiResponseException ex){
            logger.error("Cannot get object infor by its handler" , ex);
        }
        return null;
    }

    @Override
    public String[] getItemIdsByCollectionId(String id) {
        try{
            String itemCount = String.valueOf(getItemCountByCollectionId(id));
            String itemsInfo = httpRequestHandler.sendGet(dspaceApiUrl + "/collections/" + id + "/items?limit="+itemCount);
            String[] itemsInfoArr = itemsInfo.split("\\n");
            if(null != itemsInfoArr[0] && itemsInfoArr[0].equals("200")){
                    itemsInfo = itemsInfoArr[1];
            }
            else{
                throw new ErrorDspaceApiResponseException("Got the response code "+itemsInfoArr[0]);
            }
            List<Map<String, Object>> itemsList = DataUtil.getListFromJson(itemsInfo);
            if(itemsList.size() > 0){
                List<String> itemIdList = new ArrayList();
                for(Map<String, Object> item : itemsList){
                    Object idObj = item.get("id");
                    if(null != idObj && !"".equals(String.valueOf(idObj))){
                        itemIdList.add(String.valueOf(idObj));
                    }
                    else{
                        continue;
                    }
                }
                if(itemIdList.size() > 0){
                    return itemIdList.toArray(new String[itemIdList.size()]);
                }
            }
        }
        catch(ErrorDspaceApiResponseException ex){
            logger.error("Cannot get object infor by its handler" , ex);
        }
        return null;
    }

    @Override
    public String[] getItemIdsByCollectionHandler(String handler) {
        return getItemIdsByCollectionId(getObjectIdByHandler(handler));
    }
    
    @Override
    public List<Map<String, Object>> getItemMetadataById(String id){
        String metadataInfo;
        try{
            String url = dspaceApiUrl + "/items/" + id + "/metadata";
            String metadataJson = httpRequestHandler.sendGet(url);
            String[] metadataJsonArr = metadataJson.split("\\n");
            if(null != metadataJsonArr[0] && metadataJsonArr[0].equals("200")){
                    metadataInfo = metadataJsonArr[1];
            }
            else{
                throw new ErrorDspaceApiResponseException("Got the response code "+metadataJsonArr[0]);
            }
            return DataUtil.getListFromJson(metadataInfo);
        }catch(ErrorDspaceApiResponseException ex){
            logger.error("Cannot get the item metadata!", ex);
        }
        return null;
    }
    
    @Override
    public int getItemCountByCollectionHandler(String handler){
        return getItemCountByCollectionId(getObjectIdByHandler(handler));
    }
    
    @Override
    public int getItemCountByCollectionId(String id){
        try{
            String itemsInfo = httpRequestHandler.sendGet(dspaceApiUrl + "/collections/" + id);
            String[] itemsInfoArr = itemsInfo.split("\\n");
            if(null != itemsInfoArr[0] && itemsInfoArr[0].equals("200")){
                    itemsInfo = itemsInfoArr[1];
            }
            else{
                throw new ErrorDspaceApiResponseException("Got the response code "+itemsInfoArr[0]);
            }
            Map<String, Object> itemsList = DataUtil.getMapFromJson(itemsInfo);
            return (int) itemsList.get("numberItems");
        }
        catch(ErrorDspaceApiResponseException ex){
            logger.error("Cannot get object infor by its handler" , ex);
        }
        return -1;
    }
    
    /**
     * 
     * @param id : item internal id
     * @param data : is a list of maps of metadata entries: every entry should have three keys, i.e., "key", "value", and "language"
     */
    @Override
    public void updateItemMetadata(String id, List<Map<String, String>> data){
        if(null == data || data.size() == 0){
            return;
        }
        String json = "[";
        for(Map entry : data){
            if(null != entry){
                json += "{";
                json += "\"key\":\"" + (String)entry.get("key") + "\",";
                json += "\"value\":\"" + (String)entry.get("value") + "\",";
                json += "\"language\":\"" + (String)entry.get("language") + "\"";
                json += "},";
            }
        }
        json = json.substring(0, json.length()-1) + "]";
        
        Map<String, String>header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        header.put("rest-dspace-token", token);
        
        httpRequestHandler.requestWithHeaderInfo(dspaceApiUrl + "/items/" + id +"/metadata", "PUT", header, json);
    }
}
