/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.commons.recipes;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public class PageRecipe {
    private String index;
    private Map<String, Map<String, String>> recipe;
    
    public PageRecipe(){
        recipe = new HashMap<>();
    }

    public String getIndex() {
        return index;
    }

    public Map<String, Map<String, String>> getRecipe() {
        return recipe;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setRecipe(Map<String, Map<String, String>> recipe) {
        this.recipe = recipe;
    }
    
    public void setLabel(String label){
        if(null == recipe.get(index)){
            recipe.put(index, new HashMap<String, String>());
        }
        recipe.get(index).put("label", label);
    }
    
    public void setFile(String filePath){
        if(null == recipe.get(index)){
            recipe.put(index, new HashMap<String, String>());
        }
        recipe.get(index).put("file", filePath);
    }
    
    public void setMd5(String md5){
        if(null == recipe.get(index)){
            recipe.put(index, new HashMap<String, String>());
        }
        recipe.get(index).put("md5", md5);
    }
    
    public void setUuid(String uuid){
        if(null == recipe.get(index)){
            recipe.put(index, new HashMap<String, String>());
        }
        recipe.get(index).put("uuid", uuid);
    }
    
    public void setExif(String exif){
        if(null == recipe.get(index)){
            recipe.put(index, new HashMap<String, String>());
        }
        recipe.get(index).put("exif", exif);
    }
}
