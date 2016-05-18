/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.lawlibrary;

import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public interface LawLibDataHandler {
    public void readSourceData();
    public Map getData();
    public void outputMetaData();
//    public String getDspaceLoadingData(String filePath);
//    public String getDspaceLoadingData(MultipartFile file);
//    public String saveUploadedData(MultipartFile file);
}
