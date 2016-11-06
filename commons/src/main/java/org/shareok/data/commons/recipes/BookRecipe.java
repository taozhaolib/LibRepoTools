/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.commons.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Tao Zhao
 */
public class BookRecipe {
    private Map<String, Object> recipe;

    public Map<String, Object> getRecipe() {
        return recipe;
    }

    public void setRecipe(Map<String, Object> recipe) {
        this.recipe = recipe;
    }
    
    public void setImportType(String importType){
        recipe.put("import", importType);
    }
    
    public void setUpdate(String Update){
        recipe.put("update", Update);
    }
    
    public void setUuid(UUID uuid){
        recipe.put("uuid", uuid.toString());
    }
    
    public void setLabel(String label){
        recipe.put("label", label);
    }
    
    public void setMetadata(Map<String,String> metadata){
        recipe.put("metadata", metadata);
    }
    
    public void setPages(PageRecipe[] pages){
        recipe.put("pages", pages);
    }
    
    /**
     * Generates metadata data storage for the book
     * The schemas for metadata: Dubling Core, MODS, MARC
     * The Map has metadata schema as key and the file uri or path as the value
     * 
     * @return : metadata map
     */
    public Map<String, String> getMetadata(){
        Map<String, String> metadata = new HashMap<>();
        return metadata;
    }
}
