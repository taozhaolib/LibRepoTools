/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.ssh;

import com.jcraft.jsch.SftpProgressMonitor;

/**
 *
 * @author Tao Zhao
 */
public class FileProgressMonitor implements SftpProgressMonitor {
    
    private long transfered;
    private String logger;
    
    public FileProgressMonitor(long transfered){
        this.transfered = transfered;
    }

    public String getLogger() {
        return logger;
    }

    public void setTransfered(long transfered) {
        this.transfered = transfered;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    @Override
    public boolean count(long count) {
        transfered = transfered + count;
        addLogger("Currently transferred to the remote server total size: " + transfered + " bytes");
        //System.out.println("Currently transferred total size: " + transfered + " bytes");
        return true;
    }

    @Override
    public void end() {
        addLogger("Transferring done.");
        System.out.println("Transferring done.");
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        addLogger("Transferring begin.");
        System.out.println("Transferring begin.");
    }
    
    private void addLogger(String info){
        logger = logger.concat(info + "\n\n");
    }
}
