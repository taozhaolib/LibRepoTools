/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.dspacemanager.DspaceJournalDataUtil;
import org.shareok.data.dspacemanager.DspaceSshDataUtil;
import org.shareok.data.dspacemanager.DspaceSshHandler;
import org.shareok.data.kernel.api.services.dspace.DspaceSshService;
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
    
    private DspaceSshService dsSshService;

    public DspaceSshService getDsSshService() {
        return dsSshService;
    }

    @Autowired
    public void setDsSshService(DspaceSshService dsSshService) {
        this.dsSshService = dsSshService;
    }
    
    @RequestMapping(value="/ssh/dspace/journal/{publisher}/{action}", method=RequestMethod.POST)
    public ModelAndView sshDspaceJournalDataHandler(@ModelAttribute("SpringWeb")DspaceSshHandler handler, @PathVariable("publisher") String publisher, @PathVariable("action") String action) {
       
        if (null != handler) {
            try {
                String uploadFilePath = DspaceJournalDataUtil.getJournalImportFilePath(handler.getUploadFile(), publisher);
                handler.setUploadFile(uploadFilePath);
                if(null == handler.getSshExec()){
                    handler.setSshExec(DspaceSshDataUtil.getSshExecForDspace());
                }
                dsSshService.setHandler(handler);
                dsSshService.sshImportData();
                ModelAndView model = new ModelAndView();
                model.setViewName("journalDataUpload");
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
    public ModelAndView sshDspaceSafImport(@ModelAttribute("SpringWeb")DspaceSshHandler handler, @RequestParam("saf") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String uploadFile = DspaceSshDataUtil.saveUploadedData(file);
                handler.setUploadFile(uploadFile);
                ModelAndView model = new ModelAndView();
                model.addObject("view", "sshDspaceSafImport");
                model.setViewName("sshDspaceSafImport");
                if(null == handler.getSshExec()){
                    handler.setSshExec(DspaceSshDataUtil.getSshExecForDspace());
                }
                dsSshService.setHandler(handler);
                String reportPath = dsSshService.sshImportData();   
                String importTime = null;
                String failedTime = null;
                if(null != reportPath && !"".equals(reportPath) && !reportPath.startsWith("Failed import at")){
                    String reportFileName = FileUtil.getFileNameWithoutExtension(reportPath);
                    String[] pathInfo = reportFileName.split("\\/");
                    importTime = pathInfo[pathInfo.length-1];
                }
                else{
                    failedTime = reportPath;
                }
                model.addObject("reportPath", reportPath);
                model.addObject("host", handler.getHost());
//                model.addObject("port", handler.getPort());
                model.addObject("collection", handler.getCollectionId());
                model.addObject("importTime", importTime);
                model.addObject("failedTime", failedTime);
                return model;
            } catch (Exception e) {
                Logger.getLogger(JournalDataController.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            return null;
        }
        return null;
    }
    
    @RequestMapping(value="/ssh/dspace/download/{host}/{fileName}/")
    public void sshDspaceReportDownload(HttpServletResponse response, @PathVariable("host") String host,  @PathVariable("fileName") String fileName){
        
        String downloadPath = DspaceSshDataUtil.getSafDownloadLink(host, fileName);
        
        WebUtil.setupFileDownload(response, downloadPath);
        
    }
}