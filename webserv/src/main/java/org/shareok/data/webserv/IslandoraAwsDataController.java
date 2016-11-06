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
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.islandoramanager.IslandoraSshHandler;
import org.shareok.data.kernel.api.services.job.TaskManager;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.RedisJob;
import org.shareok.data.redis.server.IslandoraRepoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Tao Zhao
 */
@Controller
public class IslandoraAwsDataController {
    
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
    
    @RequestMapping(value="/s3/islandora/bag/import/page", method=RequestMethod.GET)
    public ModelAndView islandoraSshImportS3BagPage(){
        
        try {
            ModelAndView model = new ModelAndView();
            model = WebUtil.getServerList(model, serverService);
            model.addObject("repository", "islandora");
            model.addObject("action", "ssh-import");
//            model = WebUtil.getServerList(model);
            model.setViewName("s3BagImport");
            return model;
        } catch (Exception ex) {
            Logger.getLogger(SshIslandoraDataController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @RequestMapping(value="/s3/islandora/bag/{jobTypeStr}", method=RequestMethod.POST)
    public ModelAndView s3SshIslandoraImport(HttpServletRequest request, @ModelAttribute("SpringWeb")IslandoraSshHandler handler, @PathVariable("jobTypeStr") String jobTypeStr) {
        
        String bucketName = (String)request.getParameter("bucketName");
        String bagName = (String)request.getParameter("bagName");
        String recipeFileUri = ShareokdataManager.getAwsS3BucketUriPrefix()+bucketName+"/"+bagName+"/data/"+bagName+".json";
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        
        String serverId = handler.getServerId();
        IslandoraRepoServer server = (IslandoraRepoServer)serverService.findServerById(Integer.valueOf(serverId));
        if(null == serverId || serverId.equals("")){
            String serverName = (String)request.getParameter("serverName");
            if(null != serverName){
                server = (IslandoraRepoServer)serverService.findServerByName(serverName);
                handler.setServerId(String.valueOf(server.getServerId()));
            }
            
        }
        handler.setDrupalDirectory(server.getDrupalPath());
        handler.setFilePath(server.getIslandoraUploadPath());
        handler.setTmpPath(server.getTempFilePath());
        
        try {
            int jobTypeIndex = DataUtil.getJobTypeIndex(jobTypeStr, "islandora");   
            handler.setJobType(jobTypeIndex);
            RedisJob job = taskManager.execute(Long.valueOf(userId), handler, null, recipeFileUri);

            int statusIndex = job.getStatus();
            String isFinished = (statusIndex == 2 || statusIndex == 6) ? "true" : "false";

            ModelAndView model = new ModelAndView();
            model = WebUtil.getServerList(model, serverService);
            model.setViewName("jobReport");
            model.addObject("host", handler.getSshExec().getServer().getHost());
            model.addObject("collection", handler.getParentPid());
            model.addObject("status", RedisUtil.REDIS_JOB_STATUS[job.getStatus()]);
            model.addObject("isFinished", isFinished);
            model.addObject("reportPath", "/webserv/download/report/"+DataUtil.JOB_TYPES[jobTypeIndex]+"/"+String.valueOf(job.getJobId()));  
            WebUtil.outputJobInfoToModel(model, job);                
            return model;
        } catch (NumberFormatException e) {
            logger.error("Cannot import into the Islandora repository.", e);
        } catch (JsonProcessingException e) {
            logger.error("Cannot import into the Islandora repository.", e);
        }
        return null;
    }
}
