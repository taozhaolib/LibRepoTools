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
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.dspacemanager.DspaceDataUtil;
import org.shareok.data.dspacemanager.DspaceJournalDataUtil;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.job.TaskManager;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.DspaceApiJob;
import org.shareok.data.redis.job.RedisJob;
import org.shareok.data.redis.server.DspaceRepoServer;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
            String sampleSafPackageLink = DspaceJournalDataUtil.getLinkToSampleSafPackageFile();
            model = WebUtil.getServerList(model, serverService);
            model.addObject("jobType", jobType);
            model.addObject("repoType", "dspace");
            model.addObject("sampleSafPackageLink", sampleSafPackageLink);
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
                                            RedirectAttributes redirectAttrs,
                                            @PathVariable("repoTypeStr") String repoTypeStr, 
                                            @RequestParam(value = "localFile", required=false) MultipartFile file,
                                            @PathVariable("jobType") String jobType, 
                                            @ModelAttribute("SpringWeb")DspaceApiJob job) {
        ModelAndView model = new ModelAndView();
        
        String filePath = "";
        
        logger.debug("Start to process the DSpace Rest API request...");
        
        try{
            if(null == file || file.isEmpty()){
                String localFile = (String)request.getParameter("localFile");
                if(!DocumentProcessorUtil.isEmptyString(localFile)){
                    filePath = localFile;
                    String safDir = (String)request.getParameter("localSafDir");
                    if(!DocumentProcessorUtil.isEmptyString(safDir)){
                        filePath = safDir + File.separator + filePath;
                    }
                    String folder = (String)request.getParameter("localFolder");
                    if(!DocumentProcessorUtil.isEmptyString(folder)){
                        filePath = folder + File.separator + filePath;
                    }
                    String journalSearch = (String)request.getParameter("journalSearch");
                    if(null != journalSearch && journalSearch.equals("1")){
                        filePath = ShareokdataManager.getDspaceUploadPath()+ File.separator + filePath;
                    }
                    else{
                        filePath = ShareokdataManager.getOuhistoryUploadPath() + File.separator + filePath;
                    }
                }
                else{
                    filePath = (String)request.getParameter("remoteFileUri");
                }
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
        
        DspaceRepoServer server = (DspaceRepoServer)serverService.findServerById(job.getServerId());
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        job.setUserId(Long.valueOf(userId));
        job.setCollectionId(server.getPrefix() + "/" + job.getCollectionId());
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
            
            RedirectView view = new RedirectView();
            view.setContextRelative(true);
            view.setUrl("/report/job/"+String.valueOf(returnedJob.getJobId()));
            model.setView(view);                        
            redirectAttrs.addFlashAttribute("host", serverService.findServerById(returnedJob.getServerId()).getHost());
            redirectAttrs.addFlashAttribute("collection", DspaceDataUtil.DSPACE_REPOSITORY_HANDLER_ID_PREFIX + job.getCollectionId());             
            redirectAttrs.addFlashAttribute("isFinished", isFinished);
            redirectAttrs.addFlashAttribute("reportPath", DataHandlersUtil.getJobReportFilePath(DataUtil.JOB_TYPES[returnedJob.getType()], returnedJob.getJobId())); 
            WebUtil.outputJobInfoToModel(redirectAttrs, returnedJob);
        }
        catch(Exception ex){
            logger.error("Cannot process the job of DSpace import using REST API.", ex);
            model.addObject("errorMessage", "Cannot process the job of DSpace import using REST API.");
            model.setViewName("serverError");
        }
        return model;
    }
            
}
