/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.islandoramanager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.shareok.data.datahandlers.JobHandler;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.JobDao;
import org.shareok.data.redis.job.RedisJob;
import org.shareok.data.redis.server.IslandoraRepoServer;
import org.shareok.data.ssh.SshExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */
@Service
public class IslandoraSshHandler implements JobHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IslandoraSshHandler.class);
    
    private int jobType;
    private SshExecutor sshExec;
    private String reportFilePath;
    private String drupalDirectory; // the Drupal installation directory
    private String tmpPath;
    private String parentPid;
    private String localRecipeFilePath;
    private String recipeFileUri;
    private String uploadDst;
    private String serverId;
    private RedisJob job;

    @Override
    public int getJobType() {
        return jobType;
    }

    @Override
    public RedisJob getJob() {
        return job;
    }
    
    @Override
    public String getRepoType(){
        return "islandora";
    }
    
    public SshExecutor getSshExec() {
        return sshExec;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public String getDrupalDirectory() {
        return drupalDirectory;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public String getParentPid() {
        return parentPid;
    }

    public String getLocalRecipeFilePath() {
        return localRecipeFilePath;
    }

    public String getRecipeFileUri() {
        return recipeFileUri;
    }

    public String getUploadDst() {
        return uploadDst;
    }

    public String getServerId() {
        return serverId;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public static Logger getLogger() {
        return logger;
    }

    @Autowired
    public void setSshExec(SshExecutor sshExec) {
        this.sshExec = sshExec;
         if((null == sshExec.getServer() || sshExec.getServer().getServerName().equals("")) && null != serverId && !"".equals(serverId)){
             this.sshExec.setServer(RedisUtil.getServerDaoInstance().findServerById(Integer.parseInt(serverId)));
         }
    }

    public void setDrupalDirectory(String drupalDirectory) {
        this.drupalDirectory = drupalDirectory;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public void setParentPid(String parentPid) {
        this.parentPid = parentPid;
    }

    public void setLocalRecipeFilePath(String localRecipeFilePath) {
        this.localRecipeFilePath = localRecipeFilePath;
    }

    public void setRecipeFileUri(String recipeFileUri) {
        this.recipeFileUri = recipeFileUri;
    }

    @Override
    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }
    public void setUploadDst(String uploadDst) {
        this.uploadDst = uploadDst;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    @Autowired
    @Qualifier("job")
    public void setJob(RedisJob job) {
        this.job = job;
    }
    
    @Override
    public String getServerName(){
        if(null != sshExec && null != sshExec.getServer()){
            return sshExec.getServer().getServerName();
        }
        else{
            return RedisUtil.getServerDaoInstance().findServerById(Integer.parseInt(serverId)).getServerName();
        }
    }
    
    @Override
    public Map<String, String> outputJobDataByJobType(){
        Map<String, String> data = new HashMap<>();
        String entries = (String)DataUtil.JOB_TYPE_DATA_SCHEMA.get(DataUtil.JOB_TYPES[jobType]);
        for(String entry : entries.split(",")){
            try {
                Field f = IslandoraSshHandler.class.getDeclaredField(entry);
                if(null != f){
                    data.put(f.getName(), (String)f.get(this));
                }
            } catch (NoSuchFieldException | SecurityException ex) {
            } catch (IllegalArgumentException | IllegalAccessException ex) {
            }            
        }
        return data;
    }

    public String uploadFileToRepository(){
        return uploadFileToRepository(null);
    }
    
    public String uploadFileToRepository(String folderName){
        
        try{
            if(null == folderName || "".equals(folderName)){
                folderName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            }
            IslandoraRepoServer iServer = (IslandoraRepoServer)sshExec.getServer();
            String iUploadDst = iServer.getIslandoraUploadPath();
            String uploadFileName = new File(localRecipeFilePath).getName();
            String islandoraTargetFilePath = iUploadDst + File.separator + folderName + File.separator + uploadFileName;
            String newDirCommand = " bash -c \"mkdir " + iUploadDst + File.separator + folderName + "\"";
            sshExec.addReporter("Build up a new directory for the new importing: "+newDirCommand);
            sshExec.execCmd(newDirCommand);
            sshExec.upload(iUploadDst + File.separator + folderName, localRecipeFilePath);  
            sshExec.addReporter("The SAF package has been uploaded to the Islandora server: " + islandoraTargetFilePath + "\n");
            return islandoraTargetFilePath;
        }
        catch(Exception ex){
            logger.error("Cannot upload the file into Islandora repository", ex);
        }
        return null;
    }
    
    public String importIslandora(){
        try{     
            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            if(null != localRecipeFilePath && !"".equals(localRecipeFilePath)){
                recipeFileUri = uploadFileToRepository(time);
            }

            String importCommand = " drush -u 1 oubib --recipe_uri="+recipeFileUri+" --parent_collection="+parentPid+" --tmp_dir="+tmpPath+" --root=" + drupalDirectory;
            sshExec.addReporter("Importing the package into Islandora: "+importCommand);
            logger.debug("Importing the package into Islandora: "+importCommand);

            String[] commands = {importCommand};
            sshExec.execCmd(commands);
            sshExec.addReporter("The package has been imported into the Islandora repository.\n");
            logger.debug("The package has been imported into the Islandora repository.\n");

            String savedReportFilePath =  saveLoggerToFile();
            sshExec.addReporter("The importing logging information has been saved to file : " + reportFilePath);
            
            return savedReportFilePath;
        }
        catch(Exception ex){
            logger.error("Cannot import the SAF package into Islandora", ex);
            return null; 
        }
    }
    
    private String saveLoggerToFile(){
        try{
            File reportFile = new File(reportFilePath);
            if(!reportFile.exists()){
                reportFile.createNewFile();
            }
            FileUtil.outputStringToFile(sshExec.getReporter(), reportFilePath);
        }
        catch(IOException ioex){
            logger.error("Cannot save importing report!");
        }
        return reportFilePath;
    }
    
    @Override
    public void setFilePath(String uploadFile) {
        try{
            if(uploadFile.startsWith("uri--")){
                String uri = uploadFile.split("uri--")[1];
                setRecipeFileUri(uri);
            }
            else{
                setLocalRecipeFilePath(uploadFile);
            }
        }
        catch(ArrayIndexOutOfBoundsException ex){
            logger.error("Cannot set up the upload file due to array out of bound!", ex);
        }
    }
}
