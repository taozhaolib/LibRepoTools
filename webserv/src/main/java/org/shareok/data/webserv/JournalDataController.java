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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.dspacemanager.DspaceJournalDataUtil;
import org.shareok.data.kernel.api.services.dspace.DspaceJournalServiceManager;

/**
 *
 * @author Tao Zhao
 */

@Controller
public class JournalDataController {
    
    @RequestMapping("/dspace/journal/{publisher}")
    public ModelAndView journalHello(HttpServletRequest req, @PathVariable("publisher") String publisher) {
        String sampleDublinCoreLink = DspaceJournalDataUtil.getJournalSampleDublinCoreLink();
         ModelAndView model = new ModelAndView();
         model.setViewName("journalDataUpload");
         model.addObject("publisher", publisher);
         model.addObject("sampleDublinCore", sampleDublinCoreLink);
         return model;
    }
    
    @RequestMapping(value="/download/dspace/journal/{publisher}/{folderName}/{fileName}/")
    public void journalLoadingDataDownload(HttpServletResponse response, @PathVariable("publisher") String publisher, @PathVariable("folderName") String folderName, @PathVariable("fileName") String fileName){
        
        String downloadPath = DspaceJournalDataUtil.getDspaceJournalDownloadFilePath(publisher, folderName, fileName);
        
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
            Logger.getLogger(JournalDataController.class.getName()).log(Level.SEVERE, null, ioex);
        }
    }
    
    @RequestMapping(value="/dspace/journal/{publisher}/upload", method=RequestMethod.POST)
    public ModelAndView journalDataUpload(@RequestParam("file") MultipartFile file, @PathVariable("publisher") String publisher) {
       
        if (!file.isEmpty()) {
            try {
                String filePath = DspaceJournalServiceManager.getDspaceJournalDataService(publisher).getDsapceJournalLoadingFiles(file);
                // Some logic to process the file path to get download links:
                Map downloadLinks = DspaceJournalDataUtil.getDspaceJournalDownloadLinks(filePath);
                String downloadLink = (String)downloadLinks.get("loadingFile");
                String sampleDublinCoreLink = DspaceJournalDataUtil.getJournalSampleDublinCoreLink();
                String uploadFileLink = downloadLink.split("/journal/"+publisher+"/")[1];
                uploadFileLink = uploadFileLink.substring(0, uploadFileLink.length()-1);
                ModelAndView model = new ModelAndView();
                model.setViewName("journalDataUpload");
                model.addObject("oldFile", (String)downloadLinks.get("oldFile"));
                model.addObject("loadingFile", downloadLink);
                model.addObject("sampleDublinCore", sampleDublinCoreLink);
                model.addObject("publisher", publisher);
                model.addObject("uploadFile", uploadFileLink);
                return model;
            } catch (Exception e) {
                Logger.getLogger(JournalDataController.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            return null;
        }
        return null;
   }
    
    @RequestMapping(value="/download/dspace/journal/sampleDC.xml")
    public void sampleDspaceJournalDCFileDownload(HttpServletResponse response){
        
        String downloadPath = ShareokdataManager.getDspceSampleDublinCoreFileName();
        
        WebUtil.setupFileDownload(response, downloadPath);
    }
}
