/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.config;

import java.util.Map;

/**
 *
 * @author Tao Zhao
 */
public interface DataHandler {
    public void setFilePath(String filePath);
    public void setReportFilePath(String reportFilePath);
    public void loadJobInfoByJobId(long jobId);
    public int getJobType();    
    public Map<String, String> outputJobDataByJobType();
    public String getServerName();
    public String getRepoType();
}
