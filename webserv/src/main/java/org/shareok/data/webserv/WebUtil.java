/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletResponse;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Tao Zhao
 */
public class WebUtil {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WebUtil.class);
    
    public static void setupFileDownload(HttpServletResponse response, String downloadPath){
        try{
            File file = new File(downloadPath);
            if(!file.exists()){
                 String errorMessage = "Sorry. The file you are looking for does not exist";
                 System.out.println(errorMessage);
                 OutputStream outputStream = response.getOutputStream();
                 outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
                 outputStream.close();
                 return;
            }

            String mimeType= URLConnection.guessContentTypeFromName(file.getName());
            if(mimeType==null){
                System.out.println("mimetype is not detectable, will take default");
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser 
                while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() +"\""));


            /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

            response.setContentLength((int)file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
        catch(IOException ioex){
            logger.error("Cannot set up a file download.", ioex);
        }
    }
    
    public static void outputJobInfoToModel(ModelAndView model, RedisJob job){
        
        model.addObject("jobId", job.getJobId());      
        model.addObject("status", RedisUtil.REDIS_JOB_STATUS[job.getStatus()]);
        model.addObject("startTime", ShareokdataManager.getSimpleDateFormat().format(job.getStartTime()));
        model.addObject("endTime", ShareokdataManager.getSimpleDateFormat().format(job.getEndTime()));
        model.addObject("jobType", DataUtil.JOB_TYPES[job.getType()]);
        model.addObject("repoType", DataUtil.REPO_TYPES[job.getRepoType()]);
    }
    
    public static String getReportDownloadLink(String jobType, String jobId){
        String[] jobInfo = jobType.split("-");
        String repoType = jobInfo[jobInfo.length-1];
        return ShareokdataManager.getShareokdataPath()+ File.separator + repoType + File.separator + jobType + File.separator + jobId + File.separator + jobId + "-report.txt";
    }
    
}
