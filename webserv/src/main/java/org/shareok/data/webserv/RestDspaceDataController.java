/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.datahandlers.DataHandlersUtil;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.job.TaskManager;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.DspaceApiJob;
import org.shareok.data.redis.job.RedisJob;
import org.shareok.data.webserv.exceptions.JobProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Tao Zhao
 */

@Controller
public class RestDspaceDataController {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SshDspaceDataController.class);
    
    private RepoServerService serverService;
    
    private TaskManager taskManager;
    
    public RepoServerService getServerService() {
        return serverService;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
    
    @Autowired
    public void setServerService(RepoServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
    
    @RequestMapping(value="/rest/dspace/saf/page/{jobType}", method=RequestMethod.GET)
    public ModelAndView sshDspaceSaFImporterPage(@PathVariable("jobType") String jobType) {
       
        ModelAndView model = new ModelAndView();
        try {            
            model = WebUtil.getServerList(model, serverService);
            model.addObject("jobType", jobType);
            model.addObject("repoType", "dspace");
            model.setViewName("restApiImport");
        } catch (JsonProcessingException ex) {
            model.addObject("errorMessage", "Cannot get the server list");
            model.setViewName("serverError");
            logger.error("Cannot open the page of DSpace import using REST API.", ex);
        }
        return model;
    }
            
    @RequestMapping(value="/rest/{repoTypeStr}/{jobType}", method=RequestMethod.POST)
    public ModelAndView sshDspaceSaFImporter(HttpServletRequest request, 
                                            @PathVariable("repoTypeStr") String repoTypeStr, 
                                            @RequestParam(value = "localFile", required=false) MultipartFile file,
                                            @PathVariable("jobType") String jobType, 
                                            @ModelAttribute("SpringWeb")DspaceApiJob job) {
       
        ModelAndView model = new ModelAndView();
        
        String filePath = "";
        
        try{
            if(null == file){
                filePath = (String)request.getParameter("remoteFileUri");
            }
            else{
                filePath = ServiceUtil.saveUploadedFile(file, ShareokdataManager.getDspaceRestImportPath(jobType+"-"+repoTypeStr));
            }
        }
        catch(Exception ex){
            logger.error("Cannot upload the file for DSpace import using REST API.", ex);
            model.addObject("errorMessage", "Cannot get the server list");
            model.setViewName("serverError");
            return model;
        }
        
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        job.setUserId(Long.valueOf(userId));
        job.setRepoType(DataUtil.getRepoTypeIndex(repoTypeStr));
        job.setType(DataUtil.getJobTypeIndex(jobType, repoTypeStr));
        job.setStatus(Arrays.asList(RedisUtil.REDIS_JOB_STATUS).indexOf("created"));
        job.setFilePath(filePath);
        job.setStartTime(new Date());
        job.setEndTime(null);

        try{
            RedisJob returnedJob = taskManager.execute(job);
            
            if(null == returnedJob){
                throw new JobProcessingException("Null job object returned after processing!");
            }

            int statusIndex = job.getStatus();
            String isFinished = (statusIndex == 2 || statusIndex == 6) ? "true" : "false";

            model.setViewName("jobReport");
            model.addObject("host", serverService.findServerById(returnedJob.getServerId()).getHost());
            model.addObject("collection", job.getCollectionId()); 
            model.addObject("repoType", repoTypeStr.toUpperCase());
            model.addObject("isFinished", isFinished);
            model.addObject("reportPath", DataHandlersUtil.getJobReportFilePath(DataUtil.JOB_TYPES[returnedJob.getType()], returnedJob.getJobId())); 
            WebUtil.outputJobInfoToModel(model, returnedJob);
        }
        catch(Exception ex){
            logger.error("Cannot process the job of DSpace import using REST API.", ex);
            model.addObject("errorMessage", "Cannot process the job of DSpace import using REST API.");
            model.setViewName("serverError");
        }
        return model;
    }
            
}
