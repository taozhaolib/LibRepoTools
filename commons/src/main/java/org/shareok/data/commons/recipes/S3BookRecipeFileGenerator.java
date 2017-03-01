/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.commons.recipes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.EmptyCsvDataException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.shareok.data.commons.CommonsUtil;
import org.shareok.data.commons.uuid.S3BookUUIDGenerator;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import oulib.aws.s3.S3Util;

/**
 *
 * @author Tao Zhao
 */
public class S3BookRecipeFileGenerator extends RecipeFileGeneratorAbstract {
    
    private String sourceBucket;
    private String outputBucket;
    private String bookName;
    private AmazonS3 s3client;
    
    public S3BookRecipeFileGenerator(){
        super();
        if(null == s3client){
            s3client = S3Util.getS3AwsClient();
        }        
    }

    public String getSourceBucket() {
        return sourceBucket;
    }

    public String getOutputBucket() {
        return outputBucket;
    }

    public String getBookName() {
        return bookName;
    }

    public AmazonS3 getS3client() {
        return s3client;
    }

    public void setSourceBucket(String sourceBucket) {
        this.sourceBucket = sourceBucket;
    }

    public void setOutputBucket(String outputBucket) {
        this.outputBucket = outputBucket;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setS3client(AmazonS3 s3client) {
        this.s3client = s3client;
    }
    
    @Autowired
    @Qualifier("s3BookUUIDGenerator")
    public void setGenerator(S3BookUUIDGenerator generator) {
        this.generator = generator;
    }
    
    @Override
    public String getJsonByInputData(Map data) throws EmptyCsvDataException{
        if(null == data){
            throw new EmptyCsvDataException("Empty data from the csv file!");
        }
        
        String json = "";
        if(null == s3client){
            s3client = S3Util.getS3AwsClient();
        }
        
        for(Object key : data.keySet()){
            String[]keyInfo = ((String)key).split("-");
            if(null == keyInfo || keyInfo.length != 2){
                continue;
            }
            String col = keyInfo[0];
            String row = keyInfo[1];
            String value = (String)data.get(key);
            if(DocumentProcessorUtil.isEmptyString(row) || DocumentProcessorUtil.isEmptyString(col) || DocumentProcessorUtil.isEmptyString(value)){
                continue;
            }
            if(col.startsWith("File name")){
                json += getJsonByBookName(value, (String)data.get("Import Type-"+row), (String)data.get("Title-"+row),
                        (String)data.get("Update-"+row), (String)data.get("dc-"+row), (String)data.get("marc-")+row,
                        (String)data.get("mods"));
            }
        }
        
        
        return json;
    }
    
    /**
     * Generates JSON string from book name
     * 
     * @param bookName : the bagit name of the book
     * @param importType : repository object type, i.g. book, post card, large image, etc.
     * @param title : title of the book
     * @param update : if it's a new import or an update to an existing object
     * @param dc : DC file path
     * @param marc : MARC file path
     * @param mods : MODS file path
     * @return JSON string
     */
    public String getJsonByBookName(String bookName, String importType, String title, String update,
            String dc, String marc, String mods){
        
        generator.setObjectName(bookName);
        Map<String, String> manifest = getManifest(bookName);
        // Now builds up the recipe information about the book:
        UUID bookUuid = generator.getObjectUuidV5();
        BookRecipe bookRecipe = CommonsUtil.getBookRecipeInstance();
        Map<String, String> metadata = bookRecipe.getMetadata();
        metadata.put("dc", dc);
        metadata.put("mods", mods);
        metadata.put("marcxml", marc);
        bookRecipe.setRecipe(new HashMap<String, Object>());
        bookRecipe.setImportType(importType);
        bookRecipe.setLabel(title);
        bookRecipe.setUpdate(update);
        bookRecipe.setUuid(bookUuid);
        

        // Now builds up the recipe information about the pages:
        // Right now we set page index to be the counting number of page files
        // So is the page label
        int index = 0;
        List<PageRecipe> pages = new ArrayList<>();
        Map<String, String> objectKeyMap = S3Util.getBucketObjectKeyMap(sourceBucket, bookName, s3client);
        for(String obj : objectKeyMap.keySet()){
            if(obj.endsWith(".tif")){
                PageRecipe page = CommonsUtil.getPageRecipeInstance();
                page.setIndex(String.valueOf(index));
                page.setLabel("Image "+String.valueOf(index));
                page.setMd5(manifest.get(obj));
                page.setUuid(((S3BookUUIDGenerator)generator).getPageUuidByName(obj).toString());
                page.setExif("");
                pages.add(page);
                index++;                
            }
        }
        
        bookRecipe.setPages(pages.toArray(new PageRecipe[pages.size()]));
        
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(bookRecipe);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(S3BookRecipeFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return json;
    }
    
    /**
     * Read out the manifest file information and convert it into a map with keys of bookname and values of hash codes
     * 
     * @param bookName
     * @return 
     */
    public Map<String, String> getManifest(String bookName){
        
        Map<String, String> manifest = new HashMap<>();
        S3ObjectInputStream in = null;
        BufferedReader reader = null;
        String line;
        
        try{
            in = s3client.getObject(new GetObjectRequest(sourceBucket, bookName+"/manifest-md5.txt")).getObjectContent();
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                if(null != line){
                    String[] manifestInfo = line.split(" ");
                    int length = manifestInfo.length;
                    if(length < 2){
                        continue;
                    }
                    String hash = manifestInfo[0];
                    String filePath = manifestInfo[length-1];
                    if(!filePath.endsWith(".tif")){
                        continue;
                    }
//                    String fileInfo[] = filePath.split("/");
//                    String fileName = fileInfo[fileInfo.length-1];
                    manifest.put(bookName+"/"+filePath, hash);
                }
            } 
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            if(null != in){
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(S3BookRecipeFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(null != reader){
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(S3BookRecipeFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return manifest;
    }
}
