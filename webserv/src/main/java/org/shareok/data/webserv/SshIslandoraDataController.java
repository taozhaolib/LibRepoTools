/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import javax.servlet.http.HttpServletRequest;
import org.shareok.data.config.DataUtil;
import org.shareok.data.islandoramanager.IslandoraSshHandler;
import org.shareok.data.kernel.api.services.job.JobHandler;
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
public class SshIslandoraDataController {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SshDspaceDataController.class);
    
    private JobHandler jobHandler;
    
    @Autowired
    public void setJobHandler(JobHandler jobHandler) {
        this.jobHandler = jobHandler;
    }
    
    @RequestMapping(value="/ssh/islandora/book/import/page/{jobType}", method=RequestMethod.GET)
    public ModelAndView sshIslandoraImporterPage(@PathVariable("jobType") String jobType){
        
        ModelAndView model = new ModelAndView();
        model.setViewName("sshIslandoraImport");
        return model;
        
    }

    @RequestMapping(value="/ssh/islandora/book/import/job/{jobType}", method=RequestMethod.POST)
    public ModelAndView sshDspaceSafImport(HttpServletRequest request, @ModelAttribute("SpringWeb")IslandoraSshHandler handler, @RequestParam(value = "recipeLocal", required=false) MultipartFile file, @PathVariable("jobType") String jobType) {
        String recipeFileUri = (String)request.getParameter("recipeFileUri");
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        
        if ((null != file && !file.isEmpty()) || (null != recipeFileUri && !"".equals(recipeFileUri))) {
            try {
                int jobTypeIndex = DataUtil.getJobTypeIndex(jobType, "islandora");                                
                RedisJob job = jobHandler.execute(Long.valueOf(userId), jobTypeIndex, DataUtil.getRepoTypeIndex("islandora"), handler, file, recipeFileUri);
                
                ModelAndView model = new ModelAndView();
                model.setViewName("jobReport");
                model.addObject("host", handler.getHost());
                model.addObject("collection", handler.getParentPid());
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
}
