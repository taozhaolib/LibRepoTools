/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.dspacemanager.DspaceJournalDataUtil;
import org.shareok.data.dspacemanager.DspaceSshDataUtil;
import org.shareok.data.dspacemanager.DspaceSshHandler;
import org.shareok.data.kernel.api.services.dspace.DspaceSshService;
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
public class SshDspaceDataController {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SshDspaceDataController.class);
    
    private JobHandler jobHandler;
    
    private DspaceSshService dsSshService;

    public DspaceSshService getDsSshService() {
        return dsSshService;
    }

    @Autowired
    public void setDsSshService(DspaceSshService dsSshService) {
        this.dsSshService = dsSshService;
    }
    
    @Autowired
    public void setJobHandler(JobHandler jobHandler) {
        this.jobHandler = jobHandler;
    }
    
    @RequestMapping(value="/ssh/dspace/journal/{publisher}/{action}", method=RequestMethod.POST)
    public ModelAndView sshDspaceJournalDataHandler(HttpServletRequest request, @ModelAttribute("SpringWeb")DspaceSshHandler handler, @PathVariable("publisher") String publisher, @PathVariable("action") String action) {
       
        String safLink = (String)request.getParameter("saf-online");
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        if (null != handler) {
            try {
                String uploadFilePath = DspaceJournalDataUtil.getJournalImportFilePath(handler.getUploadFile(), publisher);
                RedisJob job = jobHandler.execute(Long.valueOf(userId), "dspace", "journal-ssh-import", handler, FileUtil.getMultiPartFileFromFilePath(uploadFilePath, "application/zip"), safLink);
                
                ModelAndView model = new ModelAndView();
                model.setViewName("jobReport");
                model.addObject("host", handler.getHost());
                model.addObject("collection", handler.getCollectionId());
                model.addObject("reportPath", "/webserv/download/dspace/ssh-import/"+String.valueOf(job.getJobId()));  
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
    
    @RequestMapping(value="/ssh/dspace/saf/page", method=RequestMethod.GET)
    public ModelAndView sshDspaceSaFImporterPage() {
       
        ModelAndView model = new ModelAndView();
        model.addObject("view", "sshDspaceSafImport");
        model.setViewName("sshDspaceSafImport");
        return model;
    }
    
    @RequestMapping(value="/ssh/dspace/saf/import", method=RequestMethod.POST)
    public ModelAndView sshDspaceSafImport(HttpServletRequest request, @ModelAttribute("SpringWeb")DspaceSshHandler handler, @RequestParam("saf") MultipartFile file) {
        String safLink = (String)request.getParameter("saf-online");
        String email = (String) request.getSession().getAttribute("email");
        String nickName = (String) request.getSession().getAttribute("nickname");
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        if (!file.isEmpty() || (null != safLink && !"".equals(safLink))) {
            try {
                RedisJob job = jobHandler.execute(Long.valueOf(userId), "dspace", "ssh-import", handler, file, safLink);
                
                ModelAndView model = new ModelAndView();
                model.setViewName("jobReport");
                model.addObject("host", handler.getHost());
                model.addObject("collection", handler.getCollectionId());
                model.addObject("reportPath", "/webserv/download/dspace/ssh-import/"+String.valueOf(job.getJobId()));  
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
    
    @RequestMapping(value="/download/dspace/ssh-import/{jobId}")
    public void sshDspaceReportDownload(HttpServletResponse response, @PathVariable("jobId") String jobId){
        
        String downloadPath = DspaceSshDataUtil.getSafDownloadLink(jobId);
        
        WebUtil.setupFileDownload(response, downloadPath);
        
    }
}
