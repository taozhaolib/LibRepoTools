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
    
    public FileProgressMonitor(long transfered){
        this.transfered = transfered;
    }

    @Override
    public boolean count(long count) {
        transfered = transfered + count;
        System.out.println("Currently transferred total size: " + transfered + " bytes");
        return true;
    }

    @Override
    public void end() {
        System.out.println("Transferring done.");
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        System.out.println("Transferring begin.");
    }
}
