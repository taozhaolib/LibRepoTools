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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.shareok.data.config.DataUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
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
        String safPaths = null;
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
    
    @RequestMapping(value="/journal/search/{publisher}/date", method=RequestMethod.GET)
    public ModelAndView searchJournalArticlesByDatePage(HttpServletRequest request, @PathVariable("publisher") String publisher) {
        
        ModelAndView model = new ModelAndView();
        model.setViewName("journalArticleSearchList");
        model.addObject("publisher", publisher);
        model.addObject("institutions", DataUtil.getJsonArrayFromStringArray(DataUtil.INSTITUTIONS));
        return model;
    }
    
    @RequestMapping(value="/journal/search/{publisher}/date", method=RequestMethod.POST)
    public ModelAndView searchJournalArticlesByDate(HttpServletRequest request, 
                                                    RedirectAttributes redirectAttrs, 
                                                    @RequestParam(value="endDate", required = false) String endDate, 
                                                    @PathVariable("publisher") String publisher) 
    {
        
        ModelAndView model = new ModelAndView();
        String startDate = request.getParameter("startDate");
        String affiliate = request.getParameter("affiliate");
        DspaceJournalDataService serviceObj = ServiceUtil.getDspaceJournalDataServInstanceByPublisher(publisher);
        String articlesData = serviceObj.getApiResponseByDatesAffiliate(startDate, endDate, affiliate);
        // ** escape the single quote as the jquery might have problem in parsing the data  **        
        articlesData = articlesData.replace("’", "'");
        articlesData = articlesData.replaceAll("\\'", "\\\\'");
        
        RedirectView view = new RedirectView();
        view.setContextRelative(true);
        view.setUrl("/journal/search/"+publisher+"/date");
        model.setView(view);                        
        redirectAttrs.addFlashAttribute("publisher", publisher);
        redirectAttrs.addFlashAttribute("startDate", null == startDate ? "" : startDate);
        redirectAttrs.addFlashAttribute("endDate", null == endDate ? "" : endDate);
        redirectAttrs.addFlashAttribute("institutions", DataUtil.getJsonArrayFromStringArray(DataUtil.INSTITUTIONS));
        redirectAttrs.addFlashAttribute("articles", null == articlesData ? "" : articlesData);
        return model;
    }
    
    @ResponseBody
    @RequestMapping(value = "/dspace/journal/{publisher}/saf", method=RequestMethod.POST)
    public String dspaceJournalSafPackageGenerateByDois(@RequestParam("dois") String dois) {

        String[] doisArr = dois.split(";");
        String path = ServiceUtil.generateDspaceSafPackagesByDois(doisArr);
        return path;

    }
    
    /**
     *
     * @param articleData : example "articlesStr=" + [{ "volume":"12",
     *                                                  "publication date":"2017-02-13",
     *                                                  "journal":"PLOS ONE",
     *                                                  "issue":"2",    
     *                                                  "author":"Shelly C Wu;  Elizabeth A Bergey;",
     *                                                  "affiliate":"Department of Biology, University of Oklahoma, Norman, Oklahoma, United States of America;",
     *                                                  "title":"Diatoms on the carapace of common snapping turtles",
     *                                                  "doi":"10.1371/journal.pone.0171910;"
     *                                                }] 
     *                                              + "&&columns=" + columns 
     *                                              + "&&startDate=" + startDate 
     *                                              + "&&endDate=" + endDate;
     * @param publisher : String
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/download/dspace/journal/{publisher}/search", method=RequestMethod.POST)
    public String downloadDspaceJournalSearchResults(@RequestParam("articleData") String articleData, @PathVariable("publisher") String publisher) {

        String path = "";
        try{
            List<String> columns = new ArrayList<>();
            List<List<String>> articleList = new ArrayList<>();
            String[] articleDataArr = articleData.split("&&");            
            String startDate = articleDataArr[1].replaceAll("startDate=", "");
            String endDate = articleDataArr[2].replaceAll("endDate=", "");
            String articles = articleDataArr[0].replaceAll("articlesStr=", "");
            
            String journalSearchResultDownloadPath = WebUtil.getJournalSearchResultDownloadFileName(publisher, startDate, endDate);
            File csv = new File(journalSearchResultDownloadPath);
            if(!csv.exists()){
                csv.createNewFile();
            }
            
            JSONArray arrJSON = new JSONArray(articles);
            for(int i = 0; i < arrJSON.length(); i++){
                List<String> articleRow = new ArrayList<>();
                JSONObject obj = arrJSON.getJSONObject(i);
                Iterator<?> keys = obj.keys();
                while( keys.hasNext() ) {
                    String key = (String)keys.next();
                    if(i == 0){
                        columns.add(key);
                    }
                    articleRow.add((String)obj.get(key));
                }
                if(i == 0){
                    articleList.add(columns);
                }
                articleList.add(articleRow);
            }
            
            WebUtil.writeCsvData(journalSearchResultDownloadPath, articleList);
            path = WebUtil.getJournalSearchResultDownloadFilePathLink(journalSearchResultDownloadPath);
        }
        catch(Exception ex){
            logger.error("Error in processing the article research results!", ex);
        }        
        return path;

    }
    
    @RequestMapping("/downloads/journal/search/{fileName}/")
    public void downloadDspaceJournalSearchResults(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        String path = WebUtil.getJournalSearchResultDownloadFilePathFromFileName(fileName);
        WebUtil.downloadFileFromServer(response, path);
    }
}
