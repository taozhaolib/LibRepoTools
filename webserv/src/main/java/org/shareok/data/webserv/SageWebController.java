/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.shareok.data.kernel.api.services.dspace.WebservService;
import org.shareok.data.kernel.api.services.dspace.DspaceSageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
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
@Configuration
@PropertySource("classpath:shareokdata-web.properties")
public class SageWebController{

    
    @Autowired
    private Environment env;
    
    @Autowired
    private DspaceSageService dspaceSageService;
   
    @RequestMapping("/sage")
    public ModelAndView sageHello() {
         ModelAndView model = new ModelAndView();
         model.setViewName("sageDataUpload");
         //model.addObject("message", "Hello Spring MVC Framework!");

         return model;
    }
   
   @RequestMapping(value="/sage/upload", method=RequestMethod.POST)
   public ModelAndView sageUpload(@RequestParam("file") MultipartFile file) {
       
        if (!file.isEmpty()) {
            try {
                String filePath = dspaceSageService.getSageDsapceLoadingFiles(file);
                // Some logic to process the file path to get download links:
                Map downloadLinks = WebservService.getDspaceDownloadLinks(filePath);
                
                ModelAndView model = new ModelAndView();
                model.setViewName("sageDataUpload");
                model.addObject("oldFile", (String)downloadLinks.get("oldFile"));
                model.addObject("loadingFile", (String)downloadLinks.get("loadingFile"));

                return model;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        return null;
   }
   
   @RequestMapping(value="/download/sage/{folderName}/{fileName}/")
   public void sageDownload(HttpServletResponse response, @PathVariable("folderName") String folderName, @PathVariable("fileName") String fileName) {
       System.out.println("fodler = " + folderName + "  file = " + fileName);
   }
}
