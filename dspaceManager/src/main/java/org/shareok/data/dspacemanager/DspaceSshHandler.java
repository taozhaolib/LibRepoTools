/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class DspaceSshHandler implements DataHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DspaceSshHandler.class);
    
    private String reportFilePath;
    private String uploadDst;
    private String uploadFile; // *** suppose the uploaded file is a ZIP file ***
    private String host;
    private int port;
    private String userName;
    private String password;
    private String dspaceUser;
    private String dspaceDirectory; // the DSpace installation directory
    private String collectionId;
    private SshExecutor sshExec;
    //private String mapfilePath;
    
    public String getReportFilePath(){
        return reportFilePath;
    }

    public String getUploadDst() {
        return uploadDst;
    }

    public String getUploadFile() {
        return uploadFile;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDspaceUser() {
        return dspaceUser;
    }

    public String getDspaceDirectory() {
        return dspaceDirectory;
    }

    public String getCollectionId() {
        return collectionId;
    }
    
    @Override
    public void setReportFilePath(String reportFilePath){
        this.reportFilePath = reportFilePath;
    }

    public void setUploadDst(String uploadDst) {
        this.uploadDst = uploadDst;
    }

    @Override
    public void setUploadFile(String uploadFile) {
        this.uploadFile = uploadFile;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDspaceUser(String dspaceUser) {
        this.dspaceUser = dspaceUser;
    }

    public void setDspaceDirectory(String dspaceDirectory) {
        this.dspaceDirectory = dspaceDirectory;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public SshExecutor getSshExec() {
        return sshExec;
    }

    @Autowired
    public void setSshExec(SshExecutor sshExec) {
        this.sshExec = sshExec;
    }
    
    public String importDspace(){
        try{     
            String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String uploadFileName = new File(uploadFile).getName();
            String uploadFileNameWithoutExtension = uploadFileName.split("\\.")[0];
            
            String dspaceTargetFilePath = uploadDst + File.separator + time + File.separator + uploadFileName;
            
            // Build up the commands:
            String newDirCommand = "sudo -u " + userName + " mkdir " + uploadDst + File.separator + time;
            String unzipCommand = "sudo -u " + userName + " unzip -o " + dspaceTargetFilePath + " -d " + uploadDst + File.separator + time;
            //String unzipCommand = "sudo tar -xvf " + uploadDst + File.separator + uploadFileName;
            String importCommand = "sudo " + dspaceDirectory + File.separator + "bin" + File.separator + 
                                   "dspace import --add " + "--eperson=" + dspaceUser + " --collection=" + collectionId +
                                   " --source=" + uploadDst + File.separator + time + File.separator + uploadFileNameWithoutExtension + " --mapfile=" + uploadDst +
                                   File.separator + time + File.separator + "mapfile";
            //importCommand = newDirCommand + ";;" + unzipCommand + ";;" + importCommand;
            sshExec.getSshConnector().setHost(host);
            sshExec.getSshConnector().setPort(port);
            sshExec.getSshConnector().setUserName(userName);
            sshExec.getSshConnector().setPassword(password);
            sshExec.execCmd(newDirCommand);
            sshExec.upload(uploadDst + File.separator + time, uploadFile);  
            sshExec.addLogger("The SAF package has been uploaded to the DSpace server: " + dspaceTargetFilePath + "\n");
            String[] commands = {unzipCommand, importCommand};
            sshExec.execCmd(commands);
            sshExec.addLogger("The SAF package has been imported into the DSpace repository.\n");
//            sshExec.execCmd(unzipCommand);
//            sshExec.execCmd(importCommand);
            String savedReportFilePath =  saveLoggerToFile();
            sshExec.addLogger("The importing logging information has been saved to file : " + reportFilePath);
            
            return savedReportFilePath;
        }
        catch(Exception ex){
            Logger.getLogger(DspaceSshHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null; 
        }
    }
    
    private String saveLoggerToFile(){
        try{
        File reportFile = new File(reportFilePath);
        if(!reportFile.exists()){
            reportFile.createNewFile();
        }
            FileUtil.outputStringToFile(sshExec.getLogger(), reportFilePath);
        }
        catch(IOException ioex){
            logger.error("Cannot save importing report!");
        }
        return reportFilePath;
    }
}