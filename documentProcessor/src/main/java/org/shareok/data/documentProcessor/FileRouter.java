/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import org.shareok.data.documentProcessor.exceptions.DataTypeException;

/**
 * The file types can ben handled by this software are defined in an xml file<p>
 * The program has to been able to load appropriate machine based on file types
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
     * @param type : String; for example "word" or "excel"
     * 
     * @return: String[]; for example "doc,docx"
     * 
     * @throws Exception
     */
    public String[] loadOfficeFileType(String type) throws Exception {
        
        String[] fileTypes = null;
        if(null == type || "".equals(type)) {
            throw new DataTypeException ("non-empty file extension", "emypty string/undefined");
        }
        else {
            fileTypes = DocumentProcessorUtil.getDataFromXmlByTagName(DocumentProcessorUtil.getFilePathFromResources("filetypes.xml"), type);
        }
        return fileTypes;
    }
}
