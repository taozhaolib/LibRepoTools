/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.io.File;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.ssh.SshExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public class DspaceSshDataUtil {

    public static SshExecutor getSshExecForDspace() {
        ApplicationContext context = new ClassPathXmlApplicationContext("sshContext.xml");
        SshExecutor sshExecutor = (SshExecutor) context.getBean("sshExecutor");
        return sshExecutor;
    }

    /**
     * Save the uploaded SAF file at specified path
     *
     * @param file : the uploaded file
     * @return : the path of the uploading folder
     */
    public static String saveUploadedData(MultipartFile file) {
        return DocumentProcessorUtil.saveMultipartFileByTimePath(file, ShareokdataManager.getSafUploadPath());
    }
    
    public static String createSafImportDirectory(){
        return null;
    }
}
