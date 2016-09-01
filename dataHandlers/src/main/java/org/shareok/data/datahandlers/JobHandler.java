/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.datahandlers;

import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public interface JobHandler {
    public void setFilePath(String filePath);
    public void setReportFilePath(String reportFilePath);
    public int getJobType();   
    public String getServerName();
    public String getRepoType();
    public Map<String, String> outputJobDataByJobType();    
}
