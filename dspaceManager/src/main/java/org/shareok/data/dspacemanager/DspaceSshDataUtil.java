/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.io.File;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.documentProcessor.FileUtil;
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
    
    public static String getSafDownloadLink(String host, String fileName){
        return ShareokdataManager.getReportSshDspaceImport() + File.separator + host + File.separator + fileName + ".txt";
    }

    /**
     * Save the uploaded SAF file at specified path
     *
     * @param file : the uploaded file
     * @return : the path of the uploading folder
     */
    public static String saveUploadedData(MultipartFile file) {
        return FileUtil.saveMultipartFileByTimePath(file, ShareokdataManager.getSafUploadPath());
    }
    
    public static String createSafImportDirectory(){
        return null;
    }
}
