/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 *
 * @author Tao Zhao
 */
public class DataUtil {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DataUtil.class);
    
    public static final String[] REPO_TYPES = {"unknown", "dspace", "islandora", "fedora", "hydra"};
    public static final String[] JOB_TYPES = {"unknown", "ssh-import-dspace", "rest-import-dspace", "ssh-upload-dspace", "ssh-importloaded-dspace",
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
    
    public static final String[] INSTITUTIONS = {"University of Oklahoma", "Oklahoma State University", "Universitoy of Central Oklahoma"};
    
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
     * 
     * @param jsonObj : has to be jsonobject of jsonobjects
     * @return : map of string maps
     */
    public static Map<String, Map<String, String>> getMapOfStringMapFromJsonObject(JSONObject jsonObj){
        Map<String, Map<String, String>> map = new HashMap<>();
        for(String key : jsonObj.keySet()){
            JSONObject obj = jsonObj.getJSONObject(key);
            map.put(key, getMapFromJson(obj.toString()));
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
    
    public static String getJsonArrayFromStringArray(String[] array){        
        String json = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(array);
        }
        catch(Exception ex){
            logger.error("Cannot convert the string array into json array", ex);
        }
        return json;
    }
    
    public static String getJsonFromStringList(List<String> list){
        String json = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(list);
        }
        catch(Exception ex){
            logger.error("Cannot convert the string list into json!", ex);
        }
        return json;
    }
    
    public static String getJsonFromListOfMap(List<Map<String, String>> list){
        String json = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(list);
        }
        catch(Exception ex){
            logger.error("Cannot convert the string list into json!", ex);
        }
        return json;
    }
    
    /**
     * 
     * @param filePath : file containing json text
     * @return : JSONObject
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static JSONObject getJsonObjectFromFile(String filePath) throws FileNotFoundException, IOException{
        File f = new File(filePath);
        if (f.exists()){
            InputStream is = new FileInputStream(filePath);
            String jsonTxt = IOUtils.toString(is);
            JSONObject json = new JSONObject(jsonTxt);  
            return json;
        }
        return null;
    }
}
