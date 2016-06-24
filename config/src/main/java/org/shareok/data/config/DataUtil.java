/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public class DataUtil {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DataUtil.class);
    
    public static String[] REPO_TYPES = {"unknown", "dspace", "islandora", "fedora", "hydra"};
    public static String[] JOB_TYPES = {"unknown", "ssh-import-dspace", "rest-import-dspace", "ssh-upload-dspace", "ssh-importloaded-dspace",
                                        "ssh-import-islandora"};
    public static Map<String, String> JOB_TYPE_DATA_SCHEMA = new HashMap<>();
    static {
        for(String job : DataUtil.JOB_TYPES){
            switch(job){
                case "ssh-import-dspace":
                case "ssh-importloaded-dspace":
                    String schema = "serverId,uploadDst,dspaceUser,dspaceDirectory,collectionId";
                    JOB_TYPE_DATA_SCHEMA.put("ssh-import-dspace", schema);
                    JOB_TYPE_DATA_SCHEMA.put("ssh-importloaded-dspace", schema);
                    break;
                case "ssh-upload-dspace":
                    JOB_TYPE_DATA_SCHEMA.put("ssh-upload-dspace", "serverId,uploadDst");
                    break;
                case "ssh-import-islandora":
                    JOB_TYPE_DATA_SCHEMA.put("ssh-import-islandora", "serverId,drupalDirectory,uploadDst,tmpPath,parentPid,localRecipeFilePath,recipeFileUri");
                    break;
                default:
                    break; 
            }
        }
    }
    
    public static int getJobTypeIndex(String jobType, String repoType){
        String key = jobType + "-" + repoType;
        if(null != key || !"".equals(key)){
            return Arrays.asList(JOB_TYPES).indexOf(key);
        }        
        else {
            return 0;
        }
    }
    
    public static int getRepoTypeIndex(String repoType){
        if(null == repoType || "".equals(repoType)){
            return 0;
        }
        else if(repoType.equals("dspace")){
            return 1;
        }
        else if(repoType.equals("islandora")){
            return 2;
        }
        else if(repoType.equals("fedora")){
            return 3;
        }
        else if(repoType.equals("hydra")){
            return 2;
        }
        /**
         * Some other should also be implemented here.
         */
        else {
            return 0;
        }
    }
    
    public static Map getMapFromJson(String json){
        Map<String, Object> map = new HashMap<>();
        try{
            ObjectMapper mapper = new ObjectMapper();        
            map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        } catch (IOException ex) {
            logger.error("Cannot convert the json string into map", ex);
        }
        return map;
    }
    
    /**
     * Converts an array like json string into a list
     * 
     * @param json : like a Sting [{info1:val1}, {info2:val2}]
     * @return : list of hashmaps
     */
    public static List<Map<String, Object>> getListFromJson(String json){
        List<Map<String, Object>> list = new ArrayList<>();
        try{
            ObjectMapper mapper = new ObjectMapper();
            list = mapper.readValue(json, new TypeReference<ArrayList<HashMap<String, Object>>>(){});
        }
        catch(Exception ex){
            logger.error("Cannot convert the json string into list of hashmaps", ex);
        }
        return list;
    }
}
