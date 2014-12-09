/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.msofficedata;

import java.io.File;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Tao Zhao
 */
public class FileRouter {
    
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     *
     * @param type
     * @throws Exception
     */
    public String[] loadOfficeFileType(String type) throws Exception {
        
        String[] fileTypes = null;
        if(null == type || "".equals(type)) {
            throw new Exception ("The file type parameter is empty!");
        }
        else {
            fileTypes = FileUtil.getDataFromXmlByTagName(FileUtil.getFilePathFromResources("filetypes.xml"), type);
        }
        return fileTypes;
    }
}
