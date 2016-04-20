/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.shareok.data.ssh.SshExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */
@Service
public class DspaceSshHandler {
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

    public void setUploadDst(String uploadDst) {
        this.uploadDst = uploadDst;
    }

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
    
    public void importDspace(){
        try{
        //The executing user may be "dspace" or something else, so may be a user-switching 
        //command is needed before the import command      
        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String uploadFileName = new File(uploadFile).getName();
        String uploadFileNameWithoutExtension = uploadFileName.split("\\.")[0];
        String newDirCommand = "sudo -u " + userName + " mkdir " + uploadDst + File.separator + time;
        String unzipCommand = "sudo -u " + userName + " unzip -o " + uploadDst + File.separator + time + File.separator + uploadFileName + " -d " + uploadDst + File.separator + time;
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
        sshExec.execCmd(unzipCommand);
        sshExec.execCmd(importCommand);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}