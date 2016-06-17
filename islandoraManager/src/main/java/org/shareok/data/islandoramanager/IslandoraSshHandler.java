/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.islandoramanager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.shareok.data.config.DataHandler;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.ssh.SshExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */
@Service
public class IslandoraSshHandler implements DataHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IslandoraSshHandler.class);
    
    private SshExecutor sshExec;
    private String reportFilePath;
    private String drupalDirectory; // the Drupal installation directory
    private String tmpPath;
    private String parentPid;
    private String localRecipeFilePath;
    private String recipeFileUri;
    private String uploadDst;
    
    //Properties for SshConnector
    private int port;
    private int proxyPort;
    private String host;    
    private String proxyHost;
    private String userName;
    private String proxyUserName;
    private String password;
    private String proxyPassword;
    private String passPhrase;    
    private String rsaKey;

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

    public int getPort() {
        return port;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getHost() {
        return host;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getUserName() {
        return userName;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public String getPassword() {
        return password;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public String getRsaKey() {
        return rsaKey;
    }

    @Autowired
    public void setSshExec(SshExecutor sshExec) {
        this.sshExec = sshExec;
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

    public void setPort(int port) {
        this.port = port;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }

    public void setRsaKey(String rsaKey) {
        this.rsaKey = rsaKey;
    }
    
    public String uploadFileToRepository(){
        return uploadFileToRepository(null);
    }
    
    public String uploadFileToRepository(String folderName){
        
        try{
            if(null == folderName || "".equals(folderName)){
                folderName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            }
            String uploadFileName = new File(localRecipeFilePath).getName();
            String islandoraTargetFilePath = uploadDst + File.separator + folderName + File.separator + uploadFileName;
            String newDirCommand = " bash -c \"mkdir " + uploadDst + File.separator + folderName + "\"";
            sshExec.addReporter("Build up a new directory for the new importing: "+newDirCommand);
            sshExec.execCmd(newDirCommand);
            sshExec.upload(uploadDst + File.separator + folderName, localRecipeFilePath);  
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
            setUpSshConnector();
            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            if(null != localRecipeFilePath && !"".equals(localRecipeFilePath)){
                recipeFileUri = uploadFileToRepository(time);
            }

            String importCommand = "sudo drush -u 1 oubib --recipe_uri="+recipeFileUri+" --parent_collection="+parentPid+" --tmp_dir="+tmpPath+" --root=" + drupalDirectory;
            sshExec.addReporter("Importing the package into Islandora: "+importCommand);

            String[] commands = {importCommand};
            sshExec.execCmd(commands);
            sshExec.addReporter("The package has been imported into the Islandora repository.\n");

            String savedReportFilePath =  saveLoggerToFile();
            sshExec.addReporter("The importing logging information has been saved to file : " + reportFilePath);
            
            return savedReportFilePath;
        }
        catch(Exception ex){
            logger.error("Cannot import the SAF package into DSapce", ex);
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
    public void setUploadFile(String uploadFile) {
        try{
            if(uploadFile.startsWith("uri--")){
                String uri = uploadFile.split("uri--")[1];
                setRecipeFileUri(uri);
            }
            else{
                setLocalRecipeFilePath(localRecipeFilePath);
            }
        }
        catch(ArrayIndexOutOfBoundsException ex){
            logger.error("Cannot set up the upload file due to array out of bound!", ex);
        }
    }
    
    private void setUpSshConnector(){
        sshExec.getSshConnector().setHost(host);
        sshExec.getSshConnector().setPort(port);
        sshExec.getSshConnector().setUserName(userName);
        sshExec.getSshConnector().setPassword(password);
        sshExec.getSshConnector().setRsaKey(rsaKey);
        sshExec.getSshConnector().setPassPhrase(passPhrase);
        sshExec.getSshConnector().setProxyHost(proxyHost);
        sshExec.getSshConnector().setProxyPassword(proxyPassword);
        sshExec.getSshConnector().setProxyUserName(proxyUserName);
        sshExec.getSshConnector().setProxyPort(proxyPort);
    }
}
