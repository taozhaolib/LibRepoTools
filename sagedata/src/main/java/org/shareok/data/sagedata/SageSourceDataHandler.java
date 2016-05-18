/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.io.File;
import java.util.HashMap;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public interface SageSourceDataHandler {
    public void readSourceData();
    public HashMap getData();
    public void processSourceData();
    public void outputMetaData();
    public String getDspaceLoadingData(String filePath);
    public String getDspaceLoadingData(MultipartFile file);
    public String saveUploadedData(MultipartFile file);
}
