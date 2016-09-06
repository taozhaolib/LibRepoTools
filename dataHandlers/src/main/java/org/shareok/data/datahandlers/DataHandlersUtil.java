/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.datahandlers;

import java.io.File;
import org.shareok.data.config.DataUtil;
import static org.shareok.data.config.ShareokdataManager.getShareokdataPath;
import org.shareok.data.redis.job.RedisJob;

/**
 *
 * @author Tao Zhao
 */
public class DataHandlersUtil {
    public static String getJobReportPath(String jobType, long jobId){
        String shareokdataPath = getShareokdataPath();
        String repoType = jobType.split("-")[2];
        String filePath = shareokdataPath + File.separator + repoType;
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        filePath += File.separator + jobType;
        file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        filePath += File.separator + String.valueOf(jobId);
        file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        return filePath;
    }
    
    public static String getJobReportFilePath(String jobType, long jobId){
        return getJobReportPath(jobType, jobId) + File.separator + String.valueOf(jobId) + "-report.txt";
    }
    
    public static String getJobReportFilePath(RedisJob job){
        return getJobReportPath(DataUtil.JOB_TYPES[job.getType()], job.getJobId()) + File.separator + String.valueOf(job.getJobId()) + "-report.txt";
    }
}
