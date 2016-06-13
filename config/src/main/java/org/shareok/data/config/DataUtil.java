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
    public static String[] JOB_TYPES = {"unknown", "ssh-import", "rest-import", "ssh-upload", "ssh-import-uloaded"};
    
    public static int getJobTypeIndex(String jobType){
        if(null == jobType || "".equals(jobType)){
            return 0;
        }
        else if(jobType.equals("import")){
            return 1;
        }
        else if(jobType.equals("upload")){
            return 3;
        }
        else if(jobType.equals("import-uploaded")){
            return 4;
        }
        /**
         * Some other should also be implemented here.
         */
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
