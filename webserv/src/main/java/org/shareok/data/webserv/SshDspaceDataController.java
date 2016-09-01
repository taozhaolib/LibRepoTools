/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shareok.data.config.DataUtil;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.dspacemanager.DspaceJournalDataUtil;
import org.shareok.data.dspacemanager.DspaceSshHandler;
import org.shareok.data.kernel.api.services.dspace.DspaceSshService;
import org.shareok.data.kernel.api.services.job.TaskManager;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.redis.job.RedisJob;
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
public class SshDspaceDataController {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SshDspaceDataController.class);
    
    private TaskManager taskManager;
    
    private DspaceSshService dsSshService;
    
    private RepoServerService serverService;

    public DspaceSshService getDsSshService() {
        return dsSshService;
    }

    public RepoServerService getServerService() {
        return serverService;
    }

    @Autowired
    public void setDsSshService(DspaceSshService dsSshService) {
        this.dsSshService = dsSshService;
    }

    @Autowired
    public void setServerService(RepoServerService serverService) {
        this.serverService = serverService;
    }
    
    @Autowired
    public void setJobHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
    
    @RequestMapping(value="/ssh/dspace/journal/{publisher}/{action}", method=RequestMethod.POST)
    public ModelAndView sshDspaceJournalDataHandler(HttpServletRequest request, @ModelAttribute("SpringWeb")DspaceSshHandler handler, @PathVariable("publisher") String publisher, @PathVariable("action") String action) {
       
        String safLink = (String)request.getParameter("saf-online");
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        
        String serverId = handler.getServerId();
        if(null == serverId || serverId.equals("")){
            String serverName = (String)request.getParameter("serverName");
            if(null != serverName){
                handler.setServerId(String.valueOf(serverService.findServerIdByName(serverName)));
            }
        }
        
        if (null != handler) {
            try {
                String uploadFilePath = DspaceJournalDataUtil.getJournalImportFilePath(handler.getFilePath(), publisher);
                int jobTypeIndex = DataUtil.getJobTypeIndex(action, "dspace"); 
                handler.setJobType(jobTypeIndex);
                //RedisJob job = jobHandler.execute(Long.valueOf(userId), "dspace", "ssh-import", handler, FileUtil.getMultiPartFileFromFilePath(uploadFilePath, "application/zip"), safLink);
                RedisJob job = taskManager.execute(Long.valueOf(userId), handler, FileUtil.getMultiPartFileFromFilePath(uploadFilePath, "application/zip"), safLink);
                
                int statusIndex = job.getStatus();
                String isFinished = (statusIndex == 2 || statusIndex == 6) ? "true" : "false";
                
                ModelAndView model = new ModelAndView();
                model.setViewName("jobReport");
                model.addObject("host", handler.getSshExec().getServer().getHost());
                model.addObject("collection", handler.getCollectionId()); 
                model.addObject("repoType", "DSpace");
                model.addObject("isFinished", isFinished);
                model.addObject("reportPath", "/webserv/download/report/"+DataUtil.JOB_TYPES[jobTypeIndex]+"/"+String.valueOf(job.getJobId())); 
                WebUtil.outputJobInfoToModel(model, job);
                
                return model;
            } catch (Exception e) {
                Logger.getLogger(JournalDataController.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            return null;
        }
        return null;
   }
    
    @RequestMapping(value="/ssh/dspace/saf/page/{jobType}", method=RequestMethod.GET)
    public ModelAndView sshDspaceSaFImporterPage(@PathVariable("jobType") String jobType) {
       
        ModelAndView model = new ModelAndView();
        try {            
            model = WebUtil.getServerList(model, serverService);
            model.addObject("jobType", jobType);
            model.setViewName("sshDspaceSafImport");
        } catch (JsonProcessingException ex) {
            model.addObject("errorMessage", "Cannot get the server list");
            model.setViewName("serverError");
            Logger.getLogger(SshDspaceDataController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return model;
    }
    
    @RequestMapping(value="/ssh/dspace/saf/job/{jobTypeStr}", method=RequestMethod.POST)
    public ModelAndView sshDspaceSafImport(HttpServletRequest request, @ModelAttribute("SpringWeb")DspaceSshHandler handler, @RequestParam(value = "saf", required=false) MultipartFile file, @PathVariable("jobTypeStr") String jobTypeStr) {
        String safLink = (String)request.getParameter("saf-online");
        String oldJobId = (String)request.getParameter("old-jobId");
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        
        if(null == safLink || safLink.equals("")){
            safLink = "job-" + oldJobId;
        }
        
        String serverId = handler.getServerId();
        if(null == serverId || serverId.equals("")){
            String serverName = (String)request.getParameter("serverName");
            if(null != serverName){
                handler.setServerId(String.valueOf(serverService.findServerIdByName(serverName)));
            }
        }
        
        if ((null != file && !file.isEmpty()) || (null != safLink && !"".equals(safLink))) {
            try {
                int jobTypeIndex = DataUtil.getJobTypeIndex(jobTypeStr, "dspace");   
                handler.setJobType(jobTypeIndex);
                RedisJob job = taskManager.execute(Long.valueOf(userId), handler, file, safLink);
                
                int statusIndex = job.getStatus();
                String isFinished = (statusIndex == 2 || statusIndex == 6) ? "true" : "false";
                
                ModelAndView model = new ModelAndView();
                model.setViewName("jobReport");
                model.addObject("host", handler.getSshExec().getServer().getHost());
                model.addObject("collection", handler.getCollectionId());
                model.addObject("repoType", "DSpace");
                model.addObject("isFinished", isFinished);
                model.addObject("reportPath", "/webserv/download/report/"+DataUtil.JOB_TYPES[jobTypeIndex]+"/"+String.valueOf(job.getJobId()));  
                WebUtil.outputJobInfoToModel(model, job);
                
                return model;
            } catch (Exception e) {
                logger.error("Cannot import the SAF package into the DSpace server.", e);
            }
        } else {
            return null;
        }
        return null;
    }
    
    @RequestMapping(value="/download/report/{jobType}/{jobId}")
    public void sshDspaceReportDownload(HttpServletResponse response, @PathVariable("jobType") String jobType, @PathVariable("jobId") String jobId){
        
        String downloadPath = WebUtil.getReportDownloadLink(jobType, jobId);
        
        WebUtil.setupFileDownload(response, downloadPath);
        
    }
}
