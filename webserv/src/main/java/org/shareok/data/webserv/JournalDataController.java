/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.datahandlers.DataHandlersUtil;
import org.shareok.data.dspacemanager.DspaceJournalDataUtil;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.config.ConfigService;
import org.shareok.data.kernel.api.services.dspace.DspaceJournalDataService;
import org.shareok.data.kernel.api.services.dspace.DspaceJournalServiceManager;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.webserv.exceptions.EmptyDoiInformationException;
import org.shareok.data.webserv.exceptions.IncorrectDoiResponseException;
import org.shareok.data.webserv.exceptions.NoServiceProcessDoiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author Tao Zhao
 */

@Controller
public class JournalDataController {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(JournalDataController.class);
    
    private RepoServerService serverService;
    
    private ConfigService configService;

    @Autowired
    public void setServerService(RepoServerService serverService) {
        this.serverService = serverService;
    }
    
    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
    
    @RequestMapping("/dspace/journal/{publisher}/doi")
    public ModelAndView journalDoi(HttpServletRequest req, @PathVariable("publisher") String publisher) {
        ModelAndView model = new ModelAndView();
        try {
//            String sampleDublinCoreLink = DspaceJournalDataUtil.getJournalSampleDublinCoreLink();          
            model = WebUtil.getServerList(model, serverService);
            model.setViewName("journalDoiData");
            model.addObject("publisher", publisher);
//            model.addObject("sampleDublinCore", sampleDublinCoreLink);
            return model;
        } catch (JsonProcessingException ex) {
            logger.error("Cannot get the list of the servers", ex);
            model.addObject("errorMessage", "Cannot get the list of the servers");
            model.setViewName("serverError");
            return model;
        }
    }
    
    @RequestMapping("/dspace/journal/{publisher}")
    public ModelAndView journalHello(HttpServletRequest req, @PathVariable("publisher") String publisher) {
        ModelAndView model = new ModelAndView();
        try {
            String sampleDublinCoreLink = DspaceJournalDataUtil.getJournalSampleDublinCoreLink(); 
            String sampleForSafGenerationLink = DspaceJournalDataUtil.getLinkToSampleFilesForSafPackageGeneration();
            String sampleSafPackageLink = DspaceJournalDataUtil.getLinkToSampleSafPackageFile();
            model = WebUtil.getServerList(model, serverService);
            model.setViewName("journalDataUpload");
            model.addObject("publisher", publisher);
            model.addObject("sampleDublinCore", sampleDublinCoreLink);
            model.addObject("sampleSafPackageLink", sampleSafPackageLink);
            model.addObject("sampleForSafGenerationLink", sampleForSafGenerationLink);
            return model;
        } catch (JsonProcessingException ex) {
            logger.error("Cannot get the list of the servers", ex);
            model.addObject("errorMessage", "Cannot get the list of the servers");
            model.setViewName("serverError");
            return model;
        }
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
                model = WebUtil.getServerList(model, serverService);
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
    
    @RequestMapping(value="/download/dspace/safpackage/{publisher}/{folderName}/{safDirName}/{fileName}")
    public void safPackageLoadingDataDownload(HttpServletResponse response, @PathVariable("publisher") String publisher, @PathVariable("folderName") String folderName, @PathVariable("safDirName") String safDirName, @PathVariable("fileName") String fileName){
        
        String downloadPath = DspaceJournalDataUtil.getDspaceSafPackageDownloadFilePath(publisher, folderName, safDirName, fileName);
        
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
    
    @RequestMapping(value="/dspace/safpackage/doi/generate", method=RequestMethod.POST)
    public ModelAndView safPackageGenerateByDois(HttpServletRequest request, RedirectAttributes redirectAttrs, @RequestParam(value="multiDoiUploadFile", required=false) MultipartFile file) {
        String singleDoi = (String)request.getParameter("singleDoi");
        String multiDoi = (String)request.getParameter("multiDoi");
        ByteArrayInputStream stream = null;
        
        ModelAndView model = new ModelAndView();
        RedirectView view = new RedirectView();
        view.setContextRelative(true);
        
        if ((null != file && !file.isEmpty()) || null != singleDoi || null != multiDoi) {
            String safFilePath = null;
            try {
                String[] dois;
                if(null != singleDoi){
                    String doi = (String)request.getParameter("doiInput");
                    if(null == doi || doi.equals("")){
                        throw new EmptyDoiInformationException("Empty information from single DOI submission!");
                    }
                    dois = new String[]{doi};
                }
                else if(null != multiDoi){
                    String doi = (String)request.getParameter("multiDoiInput");
                    if(null == doi || doi.equals("")){
                        throw new EmptyDoiInformationException("Empty information from multiple DOI submission!");
                    }
                    dois = doi.trim().split("\\s*;\\s*");
                }
                else if(null != file && !file.isEmpty()){
                    stream = new   ByteArrayInputStream(file.getBytes());
                    String doiString = IOUtils.toString(stream, "UTF-8");
                    dois = doiString.trim().split("\\s*\\r?\\n\\s*");
                }
                else {
                    throw new EmptyDoiInformationException("Empty DOI information from unknown DOI submission!");
                }
                if(dois != null && dois.length > 0){
                    for(String doi : dois){
                        try{
                            String[] doiInfo = DataHandlersUtil.getItemInfoByDoi(doi).split("\\n");
                            if(null == doiInfo || doiInfo.length != 2){
                                throw new IncorrectDoiResponseException("Cannot get the correct response from crossref for DOI: "+doi);
                            }
                            else if(!doiInfo[0].equals("200")){
                                throw new IncorrectDoiResponseException("The response code is "+ doiInfo[0] + " from crossref for DOI: "+doi);
                            }
                            else{
                                JSONObject obj = new JSONObject(doiInfo[1]);
                                String url = obj.getJSONObject("message").getString("URL");
                                String publisher = obj.getJSONObject("message").getString("publisher");
                                DspaceJournalDataService serviceObj = ServiceUtil.getDspaceJournalDataServInstanceByPublisher(publisher);
                                if(null == serviceObj){
                                    throw new NoServiceProcessDoiException("Cannot find the DOI processing service for publisher " + publisher + "!");
                                }
                                safFilePath = serviceObj.getDspaceJournalLoadingFilesByDoi(dois);
                                if(null == url || url.equals("")){
                                    throw new IncorrectDoiResponseException("Cannot get the URL from the crossref response for DOI: "+doi);
                                }
                            }
                        }
                        catch(IncorrectDoiResponseException | NoServiceProcessDoiException ex){
                            logger.error("Cannot get correct response from crossref by doi", ex);
                        }
                    }
                }
            }
            catch(Exception ex){
                logger.error("Cannot generate the SAF packages by the DOIs", ex);
            }
            finally{
                if(null != stream){
                    try{
                        stream.close();
                    }
                    catch(IOException ex){
                        logger.error("Cannot close the input stream when reading the file containing the DOIs", ex);
                    }
                }
            }
            if(null != safFilePath && !safFilePath.equals("")){
                redirectAttrs.addFlashAttribute("safFilePath", safFilePath);
                view.setUrl(".jsp");
                model.setView(view);
            }
            else{
                redirectAttrs.addFlashAttribute("errorMessage", "The saf file path is invalid");
                view.setUrl("journalDataUpload.jsp");
                model.setView(view);
            }
        }
        else{
            redirectAttrs.addFlashAttribute("errorMessage", "The server information is empty");
            view.setUrl("errors/serverError.jsp");
            model.setView(view);
        }
        return model;
    }
    
    @RequestMapping(value="/dspace/safpackage/{publisher}/upload", method=RequestMethod.POST)
    public ModelAndView safPackageDataUpload(RedirectAttributes redirectAttrs, @RequestParam("file") MultipartFile file, @PathVariable("publisher") String publisher) {
       
        if (!file.isEmpty()) {
            try {
                String[] filePaths = DspaceJournalServiceManager.getDspaceSafPackageDataService(publisher).getSafPackagePaths(file);
                if(null != filePaths && filePaths.length > 0){
                    
                    ModelAndView model = new ModelAndView();
                    RedirectView view = new RedirectView();
                    view.setContextRelative(true);
        
                    List<String> downloadLinks = new ArrayList<>();
                    for(String path : filePaths){
                        String link = DspaceJournalDataUtil.getDspaceSafPackageDataDownloadLinks(path);
                        downloadLinks.add(link);
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    String downloadLink = mapper.writeValueAsString(downloadLinks);
//                    String uploadFileLink = downloadLink.split("/journal/"+publisher+"/")[1];
//                    uploadFileLink = uploadFileLink.substring(0, uploadFileLink.length()-1);

                    model.setViewName("journalDataUploadDisplay");
                    model.addObject("safPackages", downloadLink);
                    model.addObject("publisher", publisher);
//                    model.addObject("uploadFile", uploadFileLink);
                    view.setUrl("/dspace/safpackage/ouhistory/display");
                    model.setView(view);
                    
                    return model;
                }
                else{
                    return null;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            return null;
        }
        return null;
    }
    
    @RequestMapping(value="/dspace/safpackage/{publisher}/display")
    public ModelAndView safPackageDataDisplay(HttpServletRequest request, @PathVariable("publisher") String publisher) {
        
        String safPackages = (String)request.getParameter("safPackages");
        String uploadFile = (String)request.getParameter("uploadFile");
        ModelAndView model = new ModelAndView();
        model.setViewName("journalDataUploadDisplay");
        model.addObject("safPackages", safPackages);
        // Hard coded publisher here. Needs to be changed!!!
        model.addObject("publisher", "OU History Journal");
        model.addObject("uploadFile", uploadFile);
        return model;
    }
    
    @RequestMapping(value="/download/dspace/journal/sampleDC.xml")
    public void sampleDspaceJournalDCFileDownload(HttpServletResponse response){
        
        String downloadPath = ShareokdataManager.getDspceSampleDublinCoreFileName();
        
        WebUtil.setupFileDownload(response, downloadPath);
    }
    
    @RequestMapping(value="/download/dspace/sample/{fileName}")
    public void dspaceSampleFilesDownload(HttpServletResponse response, @PathVariable("fileName") String fileName){
        
        String downloadPath = configService.getFileDownloadPathByNameKey(DataHandlersUtil.getFileNameKeyForDownloadPath(fileName));
        
        WebUtil.setupFileDownload(response, downloadPath);
    }
}
