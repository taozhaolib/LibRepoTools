/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.io.File;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.ssh.SshExecutor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
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
        //exec.upload("/home/vagrant", "/Users/zhao0677/Projects/shareokdata/test-load.zip");
        String uploadFileName = new File(uploadFile).getName();
        String uploadFileNameWithoutExtension = uploadFileName.split("\\.")[0];
        String unzipCommand = "sudo tar -xvf " + uploadDst + File.separator + uploadFileName;
        String importCommand = "sudo " + dspaceDirectory + File.separator + "bin" + File.separator + 
                               "dspace import --add " + "--eperson=" + dspaceUser + " --collection=" + collectionId +
                               " --source=" + uploadDst + File.separator + "  " + uploadFileNameWithoutExtension + " --mapfile=" + uploadDst +
                               File.separator + "mapfile";
        //sudo unzip /home/vagrant//Users/zhao0677/Projects/shareokdata/test-load.zip
        //sudo /srv/dspace/bin/dspace import --add --eperson=tao.zhao@ou.edu --collection=123456789/2 --source=/home/vagrant//Users/zhao0677/Projects/shareokdata/test-load.zip --mapfile=/home/vagrant/mapfile
        sshExec.getSshConnector().setHost(host);
        sshExec.getSshConnector().setPort(port);
        sshExec.getSshConnector().setUserName(userName);
        sshExec.getSshConnector().setPassword(password);
        sshExec.upload(uploadDst, uploadFile);
        sshExec.execCmd(unzipCommand);
        sshExec.execCmd(importCommand);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
