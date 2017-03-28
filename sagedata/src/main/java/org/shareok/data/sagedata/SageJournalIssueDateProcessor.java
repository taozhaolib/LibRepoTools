/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.datahandlers.DataHandlersUtil;
import org.shareok.data.documentProcessor.CsvHandler;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class SageJournalIssueDateProcessor {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SageJournalIssueDateProcessor.class);
    
    private static final String[] unparseableJournals = {"Journal of Rehabilitation and Assistive Technologies Engineering"};
    
    // These journals ONLY have a link to PDF full text and should used the first date of the month as the pub date of the issue
    private static List<String> NO_ARTICLE_PUB_DATE_JOURNALS_LIST = new ArrayList<>();
    static{
        NO_ARTICLE_PUB_DATE_JOURNALS_LIST.add("Plastic Surgery Case Studies");
        NO_ARTICLE_PUB_DATE_JOURNALS_LIST.add("Plastic Surgery");
    }
    
    @SuppressWarnings("empty-statement")
    public void retrieveSageJournalVolIssueDates(Map<String, String>processedJournalsMap){
        List<String> processedJournals = new ArrayList<>();
//        JSONObject jsonObj = getSavedSageJournalVolIssueDateInformation();
        try{
            Map<String, Map<String, String>>journalMap = getSavedSageJournalVolIssueDateInformation();
            if(null == journalMap){
                journalMap = new HashMap<>();
            }
            Document doc = null;
            try {
                doc = Jsoup.connect("http://journals.sagepub.com/action/showPublications?pageSize=20&startPage=199")
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                        .cookie("auth", "token")
                        .timeout(300000)
                        .get();
                Elements trs = doc.select("form#browsePublicationsForm").get(0).select("table").get(0).select("tbody").get(0).select("tr");
                for(Element tr : trs){
                    Element link = tr.select("td").get(1).select("a").get(0);
                    String journalName = link.text();
                    String journalLink = SageDataUtil.SAGE_HTTP_PREFIX + link.attr("href");
                    String[] linkInfo = journalLink.split("/");
                    String journalIssuesLink = SageDataUtil.SAGE_HTTP_PREFIX + "/loi/" + linkInfo[linkInfo.length-1];
                    if(null == journalMap.get(journalName)){
                        Map<String, String> infoMap = new HashMap<>();
                        infoMap.put("homeLink", journalLink);
                        infoMap.put("issueLink", journalIssuesLink);
                        journalMap.put(journalName, infoMap);
                    }
                    else{
                        Map<String, String> infoMap = journalMap.get(journalName);
                        if(null == infoMap.get("homeLink")){
                            infoMap.put("homeLink", journalLink);
                        }
                        if(null == infoMap.get("issueLink")){
                            infoMap.put("issueLink", journalIssuesLink);
                        }
                    }
                }
                int kk = 0;
                mainLoop:
                for(String journal : journalMap.keySet()){
                    System.out.println("Print out journal "+journal+" information :");
                    if(null != processedJournalsMap && (journal == null ? processedJournalsMap.get(journal) == null : journal.equals(processedJournalsMap.get(journal)))){
                        System.out.println("Journal : has already been processed!");
                        continue;
                    }
//                    if(journal.contains("Christian Education")){
//                        System.out.println("Journal name : International Journal of Health Services, cannot be processed!");
////                        continue;
//                    }
//                    if(journal.contains("Plastic Surgery")){
//                        System.out.println("Journal name : International Journal of Health Services, cannot be processed!");
//                        continue;
//                    }
                    Map<String, String> journalInfoMap = journalMap.get(journal);
                    for(String key : journalInfoMap.keySet()){
                        if(key.equals("issueLink")){
                            Document loiDdoc = null;
                            try{
                                loiDdoc = Jsoup.connect(journalInfoMap.get(key))
                                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                                    .cookie("auth", "token")
                                    .timeout(300000)
                                    .get();
                            }
                            catch(HttpStatusException ex){
                                ex.printStackTrace();
                                break;
                            }
                            Thread.sleep(2200);
                            if(null != loiDdoc){
                                Map<String, Map<String, String>> dataMap;
                                if(null != journalMap.get(journal).get("data")){
                                    dataMap = DataUtil.getMapFromJson(journalMap.get(journal).get("data"));
                                }
                                else{
                                    dataMap = new HashMap<>();
                                }
                                Elements decaseDivs = loiDdoc.select("div.decade");
                                if(null != decaseDivs && decaseDivs.size() > 0){
                                    for(Element decade : decaseDivs){
                                        Elements yearsDiv = decade.select("div.years").get(0).children();
                                        if(null != yearsDiv && yearsDiv.size() > 0){
                                            for(Element yearEle : yearsDiv){
                                                Elements volumesDiv = yearEle.select("div.volumes").get(0).children();
                                                if(null != volumesDiv && volumesDiv.size() > 0){
                                                    for(Element volumeEle : volumesDiv){
                                                        String volume = volumeEle.select("a").get(0).text().trim().split("Volume")[1].trim();
                                                        Elements issueInfoDivEles = volumeEle.select("div.js_issue");
                                                        if(null != issueInfoDivEles && issueInfoDivEles.size() > 0){                                                        
                                                        for(Element issueInfoDiv : issueInfoDivEles){
                                                            String issueText = issueInfoDiv.select("a").get(0).text();
                                                            issueText = issueText.split(", ")[0].split("Issue")[1].trim();
                                                            String oldIssueDate = "";
                                                            String issueDate = "";
                                                            if(NO_ARTICLE_PUB_DATE_JOURNALS_LIST.contains(journal)){
                                                                issueDate = "01 " + issueInfoDiv.select("span.loiIssueCoverDateText").get(0).text().trim();
                                                                oldIssueDate = issueDate;
    //                                                            if(issueDate.contains("Winter")){
    //                                                                issueDate = issueDate.replaceAll("Winter", "October");
    //                                                            }
    //                                                            if(issueDate.contains("Fall") || issueDate.contains("Autumn")){
    //                                                                issueDate = issueDate.replaceAll("Fall", "September");
    //                                                                issueDate = issueDate.replaceAll("Autumn", "September");
    //                                                            }
    //                                                            if(issueDate.contains("Summer")){
    //                                                                issueDate = issueDate.replaceAll("Summer", "April");
    //                                                            }
    //                                                            if(issueDate.contains("Spring")){
    //                                                                issueDate = issueDate.replaceAll("Spring", "January");
    //                                                            }
    //                                                            try{                                                            
    //                                                                // for date string like "01 July-October 2016"
    //                                                                if(issueDate.contains("-")){
    //                                                                    String[] dateInfo = issueDate.split("-");
    //                                                                    issueDate = dateInfo[0] + " " + dateInfo[1].split(" ")[1];
    //                                                                }
    //                                                                // for date string like "01 July/October 2016"
    //                                                                if(issueDate.contains("/")){
    //                                                                    String[] dataInfo = issueDate.split("/");
    //                                                                    issueDate = dataInfo[0] + " " + dataInfo[1].split(" ")[1];
    //                                                                }
    //                                                            }
    //                                                            catch(ArrayIndexOutOfBoundsException ex){
    //                                                                System.out.println("Journal name: "+journal);
    //                                                                System.out.println("Volume: "+volume+", issue: "+issueText);
    //                                                                System.out.println("This date string cannot be parsed: "+oldIssueDate);
    //                                                                ex.printStackTrace();
    //                                                                continue;
    //                                                            }
                                                                try{
                                                                    issueDate = "01 " + issueInfoDiv.select("span.loiIssueCoverDateText").get(0).text().trim();
                                                                    oldIssueDate = issueDate;
                                                                    issueDate = DataHandlersUtil.convertFullMonthDateStringFormat(issueDate);
                                                                }
                                                                catch(ParseException ex){
    //                                                                if(!journal.contains("OMEGA - Journal of Death and Dying")){
    //                                                                    continue;
    //                                                                }
                                                                    System.out.println("Journal name: "+journal);
                                                                    System.out.println("Volume: "+volume+", issue: "+issueText);
                                                                    System.out.println("This date string cannot be parsed: "+oldIssueDate);
                                                                    ex.printStackTrace();     
                                                                    continue;
                                                                }

                                                            }
                                                            else{
                                                                try{
                                                                    Element issueLinkEle = issueInfoDiv.select("a").get(0);
                                                                    String issueLink = issueLinkEle.attr("href");
                                                                    Document issueDoc = null;
                                                                    try{                                                                    
                                                                        issueDoc = Jsoup.connect(issueLink)
                                                                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                                                                            .cookie("auth", "token")
                                                                            .timeout(300000)
                                                                            .get();                                                                        
                                                                    }
                                                                    catch(HttpStatusException ex){
                                                                        ex.printStackTrace();
                                                                        break mainLoop;
                                                                    }
                                                                    Thread.sleep(2200);
                                                                    Elements articleDivs = issueDoc.select("div.art_title, .linkable");
                                                                    String articleLink = SageDataUtil.SAGE_HTTP_PREFIX + articleDivs.get(0).select("a.ref, .nowrap").get(0).attr("href");
                                                                    if(articleLink.contains("pdf/")){
                                                                        System.out.println("journal: "+journal+" volume="+volume+" issue="+issueText+" has ONLY PDF links!");
                                                                        try{
                                                                            issueDate = issueInfoDiv.select("span.loiIssueCoverDateText").get(0).text().trim();  
                                                                            oldIssueDate = issueDate;
                                                                            if(issueDate.contains("Winter")){
                                                                                issueDate = issueDate.replaceAll("Winter", "December");
                                                                            }
                                                                            if(issueDate.contains("Fall") || issueDate.contains("Autumn")){
                                                                                issueDate = issueDate.replaceAll("Fall", "September");
                                                                                issueDate = issueDate.replaceAll("Autumn", "September");
                                                                            }
                                                                            if(issueDate.contains("Summer")){
                                                                                issueDate = issueDate.replaceAll("Summer", "June");
                                                                            }
                                                                            if(issueDate.contains("Spring")){
                                                                                issueDate = issueDate.replaceAll("Spring", "March");
                                                                            }
                                                                            if(issueDate.contains("/")){
                                                                                String[] dataInfo = issueDate.split("/");
                                                                                String dateInfo1 = dataInfo[0].trim();
                                                                                String date;
                                                                                String month1;
                                                                                String[] dateInfo1Arr = dateInfo1.split(" ");
                                                                                if(dateInfo1Arr.length == 2){
                                                                                    date = dateInfo1Arr[0];
                                                                                    month1 = dateInfo1Arr[1];
                                                                                }
                                                                                else{
                                                                                    date = "01";
                                                                                    month1 = dataInfo[0].trim();
                                                                                }             
                                                                                String month2 = dataInfo[1].split("\\s+")[0];
                                                                                String year = dataInfo[1].split("\\s+")[1];
                                                                                String date1 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month1 + " " + year);
                                                                                String date2 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month2 + " " + year);
                                                                                issueDate = date1 + "::" + date2;
                                                                            }
                                                                            //  The Journal of Psychiatry & Law dd MMMM-MMMM yyyy pattern
                                                                            else if(issueDate.contains("-")){
                                                                                if(journal.equals("OMEGA - Journal of Death and Dying")){
                                                                                    Document articleDoc = null;
                                                                                    try{
                                                                                        articleDoc = Jsoup.connect(articleLink)
                                                                                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                                                                                            .cookie("auth", "token")
                                                                                            .timeout(300000)
                                                                                            .get();
                                                                                    }
                                                                                    catch(HttpStatusException ex){
                                                                                        ex.printStackTrace();
                                                                                        break mainLoop;
                                                                                    }
                                                                                    Thread.sleep(2200);
                                                                                    Element pubDateDiv = articleDoc.select("div.published-dates").get(0);
                                                                                    issueDate = pubDateDiv.text().split("Issue published:")[1].trim();
                                                                                    oldIssueDate = issueDate;
                                                                                    issueDate = DataHandlersUtil.convertFullMonthDateStringFormat(issueDate);
                                                                                }
                                                                                else{
                                                                                    String[] dataInfo = issueDate.split("-");
                                                                                    String dateInfo1 = dataInfo[0].trim();
                                                                                    String date;
                                                                                    String month1;
                                                                                    String[] dateInfo1Arr = dateInfo1.split(" ");
                                                                                    if(dateInfo1Arr.length == 2){
                                                                                        date = dateInfo1Arr[0].trim();
                                                                                        month1 = dateInfo1Arr[1].trim();
                                                                                    }
                                                                                    else{
                                                                                        date = "01";
                                                                                        month1 = dataInfo[0].trim();
                                                                                    }             
                                                                                    String month2 = dataInfo[1].split("\\s+")[0];
                                                                                    String year = dataInfo[1].split("\\s+")[1];
                                                                                    String date1 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month1 + " " + year);
                                                                                    String date2 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month2 + " " + year);
                                                                                    issueDate = date1 + "::" + date2;
                                                                                }                                                                                
                                                                            }
                                                                            else{
                                                                                issueDate = "01 " + issueDate;                                                                            
                                                                                issueDate = DataHandlersUtil.convertFullMonthDateStringFormat(issueDate);
                                                                            }
                                                                        }
                                                                        catch(ParseException | ArrayIndexOutOfBoundsException ex){
                                                                            System.out.println("Journal name: "+journal);
                                                                            System.out.println("Volume: "+volume+", issue: "+issueText);
                                                                            System.out.println("This date string cannot be parsed: "+issueDate);
                                                                            ex.printStackTrace();    
                                                                            continue;
                                                                        }
                                                                    }
                                                                    else{
                                                                        Document articleDoc = null;
                                                                        try{
                                                                            articleDoc = Jsoup.connect(articleLink)
                                                                                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                                                                                .cookie("auth", "token")
                                                                                .timeout(300000)
                                                                                .get();
                                                                        }
                                                                        catch(HttpStatusException ex){
                                                                            ex.printStackTrace();
                                                                            break mainLoop;
                                                                        }
                                                                        Thread.sleep(2200);
                                                                        Element pubDateDiv = articleDoc.select("div.published-dates").get(0);
                                                                        issueDate = pubDateDiv.text().split("Issue published:")[1].trim();
                                                                        oldIssueDate = issueDate;
                                                                        issueDate = DataHandlersUtil.convertFullMonthDateStringFormat(issueDate);
                                                                    }

                                                                }
                                                                catch(Exception ex){
                                                                    logger.error("Cannot get the issue date for journal ="+journal+" volume="+volume+" issue="+issueText+" date="+oldIssueDate, ex);
                                                                    continue;
                                                                }
                                                            }
                                                            if(DataHandlersUtil.datesCompare(issueDate, "2010-01-01") < 0){
                                                                if(dataMap.size() > 0){
                                                                    ObjectMapper mapper = new ObjectMapper();
                                                                    String json = mapper.writeValueAsString(dataMap);
                                                                    journalInfoMap.put("data", json);
                                                                }
                                                                processedJournals.add(journal);
                                                                continue mainLoop;
                                                            }
                                                            try{
                                                                if(null != dataMap && dataMap.size() > 0 && null != dataMap.get(volume) && null != dataMap.get(volume).get(issueText)){
                                                                    continue;
                                                                }
                                                                else{
                                                                    Map<String, String> issueMap = dataMap.get(volume);
                                                                    if(null == issueMap){
                                                                        issueMap = new HashMap<>();
                                                                        issueMap.put(issueText, issueDate);
                                                                        dataMap.put(volume, issueMap);
                                                                    }               
                                                                    else{
                                                                        issueMap.put(issueText, issueDate);
                                                                    }
                                                                    System.out.println("This is vol. "+volume+" and issue "+issueText+" and date "+issueDate);
                                                                }
                                                            }
                                                            catch(Exception ex){
                                                                System.out.println("Cannot add the pub date info into data map for vol. "+volume+" and issue "+issueText+" and date "+issueDate);
                                                            }                                                       
                                                        }                                                    
                                                    }
                                                    }
                                                }
                                            }
                                        }
                                        
                                    }
                                }
                                if(dataMap.size() > 0){
                                    ObjectMapper mapper = new ObjectMapper();
                                    String json = mapper.writeValueAsString(dataMap);
                                    journalInfoMap.put("data", json);
                                }
                            }
                            
                        }
                    }
                    processedJournals.add(journal);
                    if(kk > 100){
                        break;
                    }
                    kk++;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(journalMap);
            String sageJournalIssueDateInfoFilePath = ShareokdataManager.getSageJournalIssueDateInfoFilePath();
            File sageFile = new File(sageJournalIssueDateInfoFilePath);
            if(sageFile.exists()){
                String sageJournalIssueDateInfoFilePathOld = sageJournalIssueDateInfoFilePath.split("\\.")[0] + "_" + DataHandlersUtil.getCurrentTimeString() + ".json";
                sageFile.renameTo(new File(sageJournalIssueDateInfoFilePathOld));
            }
            DocumentProcessorUtil.outputStringToFile(json, ShareokdataManager.getSageJournalIssueDateInfoFilePath());
            System.out.println("processed journals = "+mapper.writeValueAsString(processedJournals));
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Map<String, Map<String, String>> getSavedSageJournalVolIssueDateInformation(){
        try {
            Map<String, Map<String, String>> map = new HashMap<>();
            String sageJournalIssueDateInfoFilePath = ShareokdataManager.getSageJournalIssueDateInfoFilePath();
            File f = new File(sageJournalIssueDateInfoFilePath);
            if (f.exists()){
                InputStream is = new FileInputStream(sageJournalIssueDateInfoFilePath);
                String jsonTxt = IOUtils.toString(is);
                JSONObject json = new JSONObject(jsonTxt);       
                return DataUtil.getMapOfStringMapFromJsonObject(json);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(SageJournalIssueDateProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Map<String, String> getIssueDateProcessedSageJournals(Map<String, Map<String, String>> sageJournalVolIssueDateData){
        Map<String, String> processedJournals = new HashMap<>();
        for(String journal : sageJournalVolIssueDateData.keySet()){
            Map<String, String> journalData = sageJournalVolIssueDateData.get(journal);
            if(null != journalData && null != journalData.get("data")){
                processedJournals.put(journal, journal);
            }
        }
        return processedJournals;
    }
    
    /**
     * Sage journal "OMEGA - Journal of Death and Dying" has some date information cannot be parsed: e.g.  01 January 2003-2004
     * @param journalVolumeIssueData : json object loaded from sageJournalIssueDateInfo.json 
     * @param volume : volume string
     * @param issue : issue string
     * @return : a string map of start and end dates
     */
    public Map<String, String> getStartEndDatesForOMEGAJournalOfDeathAndDying(JSONObject journalVolumeIssueData, String volume, String issue){
        Map<String, String> datesMap = new HashMap<>();
        try{
            String issueDateStr = journalVolumeIssueData.getJSONObject("OMEGA - Journal of Death and Dying").getJSONObject("data").getJSONObject(volume).getString(issue);
            String[] issueDateStrInfo = issueDateStr.split("-");
            String[] info = issueDateStrInfo[0].split(" ");
            String endDate = info[0] + " " + info[1] + " " + issueDateStrInfo[1];
            datesMap.put("startDate", issueDateStrInfo[0]);
            datesMap.put("endDate", endDate);
            return datesMap;
        }
        catch(Exception ex){
            logger.error("Cannot parse the date information of journal : OMEGA - Journal of Death and Dying", ex);
        }
        return null;
    }
    
    /**
     * 
     * @param missingIssueDatesFile : a csv file contains the dates of certain journals and issues missing
     * 
     */
    public void updateSageJournalIssueDatesDataFromMissingIssueDatesFile(String missingIssueDatesFile) throws JsonProcessingException, IOException{
        try{
            Map<String, Map<String, Map<String, String>>> missingDataMap = new HashMap<>();
            Map<String, Map<String, String>> map = getSavedSageJournalVolIssueDateInformation();
            ApplicationContext context = new ClassPathXmlApplicationContext("documentProcessorContext.xml");
            CsvHandler csv = (CsvHandler)context.getBean("csvHandler");
            csv.setFileName(missingIssueDatesFile);
            csv.readData();      
            Map<String, String> missingDatesData = csv.getData();
            for(String key : missingDatesData.keySet()){
                if(key.startsWith("journal")){
                    String[] info = key.split("-");                    
                    String row = info[1];
                    String journal = missingDatesData.get("journal-"+row);
                    String volume = missingDatesData.get("volume-"+row);
                    String issue = missingDatesData.get("issue-"+row);
                    String missingData = missingDatesData.get("data-"+row);
                    String newDate = parsingSageJournalIssueDate(missingData);

                    Map<String, Map<String, String>>missingJournalData = missingDataMap.get(journal);
                    if(null == missingJournalData){
                        missingJournalData = new HashMap<>();
                        missingDataMap.put(journal, missingJournalData);
                    }
                    Map<String, String> missingVolData = missingJournalData.get(volume);
                    if(null == missingVolData){
                        missingVolData = new HashMap<>();
                        missingJournalData.put(volume, missingVolData);
                    }
                    missingVolData.put(issue, newDate);                    
                }
            }
            
            for(String journal : missingDataMap.keySet()){
                Map<String, Map<String, String>> missingVolData = missingDataMap.get(journal);
                Map<String, String> journalMap = map.get(journal);
                if(null == journalMap){
                    journalMap = new HashMap<>();
                    journalMap.put("data", "{}");
                    map.put(journal, journalMap);
                    continue;
                }
                String data = journalMap.get("data");
                JSONObject dataJson = new JSONObject(data);
                for(String volume : missingVolData.keySet()){
                    Map<String, String> missingIssueMap = missingVolData.get(volume);
                    JSONObject volDataJason = null;
                    if(dataJson.has(volume)){
                        volDataJason = dataJson.getJSONObject(volume);
                    }
                    else{
                        volDataJason = new JSONObject();
                        dataJson.put(volume, volDataJason);
                    }
                    for(String issue : missingIssueMap.keySet()){
                        volDataJason.put(issue, missingIssueMap.get(issue));
                    }
                }                
                String json = dataJson.toString();
                journalMap.put("data", json);
            }
            System.out.println("All missing issues have been added into journal data map.");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(map);
            String sageJournalIssueDateInfoFilePath = ShareokdataManager.getSageJournalIssueDateInfoFilePath();
            File sageFile = new File(sageJournalIssueDateInfoFilePath);
            if(sageFile.exists()){
                String sageJournalIssueDateInfoFilePathOld = sageJournalIssueDateInfoFilePath.split("\\.")[0] + "_" + DataHandlersUtil.getCurrentTimeString() + ".json";
                sageFile.renameTo(new File(sageJournalIssueDateInfoFilePathOld));
            }
            DocumentProcessorUtil.outputStringToFile(json, ShareokdataManager.getSageJournalIssueDateInfoFilePath());
        }
        catch(BeansException | JSONException ex){
            ex.printStackTrace();
        }
    }
    
    public Map<String, Map<String, String>> updateSageJournalLinks(Map<String, Map<String, String>> journalMap){
        Document doc = null;
        try {
            doc = Jsoup.connect("http://journals.sagepub.com/action/showPublications?pageSize=20&startPage=199")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .cookie("auth", "token")
                    .timeout(300000)
                    .get();
            Elements trs = doc.select("form#browsePublicationsForm").get(0).select("table").get(0).select("tbody").get(0).select("tr");
            for(Element tr : trs){
                Element link = tr.select("td").get(1).select("a").get(0);
                String journalName = link.text();
                String journalLink = SageDataUtil.SAGE_HTTP_PREFIX + link.attr("href");
                String[] linkInfo = journalLink.split("/");
                String journalIssuesLink = SageDataUtil.SAGE_HTTP_PREFIX + "/loi/" + linkInfo[linkInfo.length-1];
                if(null == journalMap.get(journalName)){
                    Map<String, String> infoMap = new HashMap<>();
                    infoMap.put("homeLink", journalLink);
                    infoMap.put("issueLink", journalIssuesLink);
                    journalMap.put(journalName, infoMap);
                }
                else{
                    Map<String, String> infoMap = journalMap.get(journalName);
                    if(null == infoMap.get("homeLink")){
                        infoMap.put("homeLink", journalLink);
                    }
                    if(null == infoMap.get("issueLink")){
                        infoMap.put("issueLink", journalIssuesLink);
                    }
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return journalMap;
    }
    
    public String parsingSageJournalIssueDate(String oldDate){
        String originalOldDate = oldDate;
        String newDate = "";
        try{
            if(!Character.isDigit(oldDate.charAt(0))){
                oldDate = "01 "+oldDate;
            }
            if(oldDate.contains("/")){
                String[] dataInfo = oldDate.split("/");
                String dateInfo1 = dataInfo[0].trim();
                String date;
                String month1;
                String[] dateInfo1Arr = dateInfo1.split(" ");
                date = dateInfo1Arr[0];
                month1 = dateInfo1Arr[1];            
                String month2 = dataInfo[1].split("\\s+")[0];
                String year = dataInfo[1].split("\\s+")[1];
                String date1 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month1 + " " + year);
                String date2 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month2 + " " + year);
                newDate = date1 + "::" + date2;
            }
            else if(oldDate.contains("-")){
                String[] dataInfo = oldDate.split("-");
                String dateInfo1 = dataInfo[0].trim();
                String date;
                String month1;
                String[] dateInfo1Arr = dateInfo1.split(" ");
                date = dateInfo1Arr[0].trim();
                month1 = dateInfo1Arr[1].trim();            
                String month2 = dataInfo[1].split("\\s+")[0];
                String year = dataInfo[1].split("\\s+")[1];
                String date1 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month1 + " " + year);
                String date2 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month2 + " " + year);
                newDate = date1 + "::" + date2;
            }
            else if(oldDate.contains("&")){
                String[] dataInfo = oldDate.split("&");
                String dateInfo1 = dataInfo[0].trim();
                String dateInfo2 = dataInfo[1].trim();
                String date;
                String month1;
                String[] dateInfo1Arr = dateInfo1.split(" ");
                date = dateInfo1Arr[0].trim();
                month1 = dateInfo1Arr[1].trim();            
                String month2 = dateInfo2.split("\\s+")[0];
                String year = dateInfo2.split("\\s+")[1];
                String date1 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month1 + " " + year);
                String date2 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month2 + " " + year);
                newDate = date1 + "::" + date2;
            }
        }
        catch(ParseException | ArrayIndexOutOfBoundsException ex){
            System.out.println("Cannot parse the date: "+originalOldDate);
            ex.printStackTrace();
        }
        return newDate;
    }
    
    /**
     * Somehow some of the issue keys have comma after the issue digit number. Need to find out the reason.
     */
    public void cleanIssueKeys(){
        Map<String, Map<String, String>> map = getSavedSageJournalVolIssueDateInformation();
        String journalName = "";
        String data = "";
        try{
            for(String journal : map.keySet()){
                journalName = journal;
                Map<String, String> journalMap = map.get(journal);                
                data = journalMap.get("data");
                if(DocumentProcessorUtil.isEmptyString(data)){
                    System.out.println("Journal "+journal+" has not data to process!");
                    continue;
                }
                JSONObject dataJson = new JSONObject(data);                
                for(String volume : dataJson.keySet()){
                    JSONObject volJson = dataJson.getJSONObject(volume);
                    List<String> wrongKeys = new ArrayList<>();
                    for(String issue : volJson.keySet()){
                        if(issue.contains(",")){
                            wrongKeys.add(issue);
                        }
                    }
                    for(String wrongKey : wrongKeys){
                        String newKey = wrongKey.split("\\,+")[0];
                        String wrongKeyData = volJson.getString(wrongKey);
                        volJson.remove(wrongKey);
                        volJson.put(newKey, wrongKeyData);
                    }
                }   
                data = dataJson.toString();
                journalMap.put("data", data);                
            }
            System.out.println("The data map has been cleaned");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(map);
            String sageJournalIssueDateInfoFilePath = ShareokdataManager.getSageJournalIssueDateInfoFilePath();
            File sageFile = new File(sageJournalIssueDateInfoFilePath);
            if(sageFile.exists()){
                String sageJournalIssueDateInfoFilePathOld = sageJournalIssueDateInfoFilePath.split("\\.")[0] + "_" + DataHandlersUtil.getCurrentTimeString() + ".json";
                sageFile.renameTo(new File(sageJournalIssueDateInfoFilePathOld));
            }
            DocumentProcessorUtil.outputStringToFile(json, ShareokdataManager.getSageJournalIssueDateInfoFilePath());
        }
        catch(NullPointerException ex){
            System.out.println("This data "+data+" for journal "+journalName+" gives null pointer exception");
            ex.printStackTrace();
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SageJournalIssueDateProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SageJournalIssueDateProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    public void updateSageJournalIssueDatesData(Map<String, Map<String, String>> journalMap) throws InterruptedException, IOException{
//        int kk = 0;
//        mainLoop:
//        for(String journal : journalMap.keySet()){
//            System.out.println("Print out journal "+journal+" information :");
//            Map<String, String> journalInfoMap = journalMap.get(journal);
//            for(String key : journalInfoMap.keySet()){
//                if(key.equals("issueLink")){
//                    Document loiDdoc = null;
//                    try{
//                        loiDdoc = Jsoup.connect(journalInfoMap.get(key))
//                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
//                            .cookie("auth", "token")
//                            .timeout(300000)
//                            .get();
//                    }
//                    catch(HttpStatusException ex){
//                        ex.printStackTrace();
//                        break;
//                    }
//                    Thread.sleep(2200);
//                    if(null != loiDdoc){
//                        Map<String, Map<String, String>> dataMap;
//                        if(null != journalMap.get(journal).get("data")){
//                            dataMap = DataUtil.getMapFromJson(journalMap.get(journal).get("data"));
//                        }
//                        else{
//                            dataMap = new HashMap<>();
//                        }
//                        Elements decaseDivs = loiDdoc.select("div.decade");
//                        if(null != decaseDivs && decaseDivs.size() > 0){
//                            for(Element decade : decaseDivs){
//                                Elements yearsDiv = decade.select("div.years").get(0).children();
//                                if(null != yearsDiv && yearsDiv.size() > 0){
//                                    for(Element yearEle : yearsDiv){
//                                        Elements volumesDiv = yearEle.select("div.volumes").get(0).children();
//                                        if(null != volumesDiv && volumesDiv.size() > 0){
//                                            for(Element volumeEle : volumesDiv){
//                                                String volume = volumeEle.select("a").get(0).text().trim().split("Volume")[1].trim();
//                                                Elements issueInfoDivEles = volumeEle.select("div.js_issue");
//                                                if(null != issueInfoDivEles && issueInfoDivEles.size() > 0){                                                        
//                                                for(Element issueInfoDiv : issueInfoDivEles){
//                                                    String issueText = issueInfoDiv.select("a").get(0).text();
//                                                    issueText = issueText.split(", ")[0].split("Issue")[1].trim();
//                                                    String oldIssueDate = "";
//                                                    String issueDate = "";
//                                                    if(NO_ARTICLE_PUB_DATE_JOURNALS_LIST.contains(journal)){
//                                                        issueDate = "01 " + issueInfoDiv.select("span.loiIssueCoverDateText").get(0).text().trim();
//                                                        oldIssueDate = issueDate;
//                                                        try{
//                                                            issueDate = "01 " + issueInfoDiv.select("span.loiIssueCoverDateText").get(0).text().trim();
//                                                            oldIssueDate = issueDate;
//                                                            issueDate = DataHandlersUtil.convertFullMonthDateStringFormat(issueDate);
//                                                        }
//                                                        catch(ParseException ex){
////                                                                if(!journal.contains("OMEGA - Journal of Death and Dying")){
////                                                                    continue;
////                                                                }
//                                                            System.out.println("Journal name: "+journal);
//                                                            System.out.println("Volume: "+volume+", issue: "+issueText);
//                                                            System.out.println("This date string cannot be parsed: "+oldIssueDate);
//                                                            ex.printStackTrace();     
//                                                            continue;
//                                                        }
//
//                                                    }
//                                                    else{
//                                                        try{
//                                                            Element issueLinkEle = issueInfoDiv.select("a").get(0);
//                                                            String issueLink = issueLinkEle.attr("href");
//                                                            Document issueDoc = null;
//                                                            try{                                                                    
//                                                                issueDoc = Jsoup.connect(issueLink)
//                                                                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
//                                                                    .cookie("auth", "token")
//                                                                    .timeout(300000)
//                                                                    .get();                                                                        
//                                                            }
//                                                            catch(HttpStatusException ex){
//                                                                ex.printStackTrace();
//                                                                break mainLoop;
//                                                            }
//                                                            Thread.sleep(2200);
//                                                            Elements articleDivs = issueDoc.select("div.art_title, .linkable");
//                                                            String articleLink = SageDataUtil.SAGE_HTTP_PREFIX + articleDivs.get(0).select("a.ref, .nowrap").get(0).attr("href");
//                                                            if(articleLink.contains("pdf/")){
//                                                                System.out.println("journal: "+journal+" volume="+volume+" issue="+issueText+" has ONLY PDF links!");
//                                                                try{
//                                                                    issueDate = issueInfoDiv.select("span.loiIssueCoverDateText").get(0).text().trim();  
//                                                                    oldIssueDate = issueDate;
//                                                                    if(issueDate.contains("Winter")){
//                                                                        issueDate = issueDate.replaceAll("Winter", "December");
//                                                                    }
//                                                                    if(issueDate.contains("Fall") || issueDate.contains("Autumn")){
//                                                                        issueDate = issueDate.replaceAll("Fall", "September");
//                                                                        issueDate = issueDate.replaceAll("Autumn", "September");
//                                                                    }
//                                                                    if(issueDate.contains("Summer")){
//                                                                        issueDate = issueDate.replaceAll("Summer", "June");
//                                                                    }
//                                                                    if(issueDate.contains("Spring")){
//                                                                        issueDate = issueDate.replaceAll("Spring", "March");
//                                                                    }
//                                                                    if(issueDate.contains("/")){
//                                                                        String[] dataInfo = issueDate.split("/");
//                                                                        String dateInfo1 = dataInfo[0].trim();
//                                                                        String date;
//                                                                        String month1;
//                                                                        String[] dateInfo1Arr = dateInfo1.split(" ");
//                                                                        if(dateInfo1Arr.length == 2){
//                                                                            date = dateInfo1Arr[0];
//                                                                            month1 = dateInfo1Arr[1];
//                                                                        }
//                                                                        else{
//                                                                            date = "01";
//                                                                            month1 = dataInfo[0].trim();
//                                                                        }             
//                                                                        String month2 = dataInfo[1].split("\\s+")[0];
//                                                                        String year = dataInfo[1].split("\\s+")[1];
//                                                                        String date1 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month1 + " " + year);
//                                                                        String date2 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month2 + " " + year);
//                                                                        issueDate = date1 + "::" + date2;
//                                                                    }
//                                                                    //  The Journal of Psychiatry & Law dd MMMM-MMMM yyyy pattern
//                                                                    else if(issueDate.contains("-")){
//                                                                        String[] dataInfo = issueDate.split("-");
//                                                                        String dateInfo1 = dataInfo[0].trim();
//                                                                        String date;
//                                                                        String month1;
//                                                                        String[] dateInfo1Arr = dateInfo1.split(" ");
//                                                                        if(dateInfo1Arr.length == 2){
//                                                                            date = dateInfo1Arr[0].trim();
//                                                                            month1 = dateInfo1Arr[1].trim();
//                                                                        }
//                                                                        else{
//                                                                            date = "01";
//                                                                            month1 = dataInfo[0].trim();
//                                                                        }             
//                                                                        String month2 = dataInfo[1].split("\\s+")[0];
//                                                                        String year = dataInfo[1].split("\\s+")[1];
//                                                                        String date1 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month1 + " " + year);
//                                                                        String date2 = DataHandlersUtil.convertFullMonthDateStringFormat(date + " " + month2 + " " + year);
//                                                                        issueDate = date1 + "::" + date2;
//                                                                    }
//                                                                    else{
//                                                                        issueDate = "01 " + issueDate;                                                                            
//                                                                        issueDate = DataHandlersUtil.convertFullMonthDateStringFormat(issueDate);
//                                                                    }
//                                                                }
//                                                                catch(ParseException | ArrayIndexOutOfBoundsException ex){
//                                                                    System.out.println("Journal name: "+journal);
//                                                                    System.out.println("Volume: "+volume+", issue: "+issueText);
//                                                                    System.out.println("This date string cannot be parsed: "+issueDate);
//                                                                    ex.printStackTrace();    
//                                                                    continue;
//                                                                }
//                                                            }
//                                                            else{
//                                                                Document articleDoc = null;
//                                                                try{
//                                                                    articleDoc = Jsoup.connect(articleLink)
//                                                                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
//                                                                        .cookie("auth", "token")
//                                                                        .timeout(300000)
//                                                                        .get();
//                                                                }
//                                                                catch(HttpStatusException ex){
//                                                                    ex.printStackTrace();
//                                                                    break mainLoop;
//                                                                }
//                                                                Thread.sleep(2200);
//                                                                Element pubDateDiv = articleDoc.select("div.published-dates").get(0);
//                                                                issueDate = pubDateDiv.text().split("Issue published:")[1].trim();
//                                                                oldIssueDate = issueDate;
//                                                                issueDate = DataHandlersUtil.convertFullMonthDateStringFormat(issueDate);
//                                                            }
//
//                                                        }
//                                                        catch(Exception ex){
//                                                            logger.error("Cannot get the issue date for journal ="+journal+" volume="+volume+" issue="+issueText+" date="+oldIssueDate, ex);
//                                                            continue;
//                                                        }
//                                                    }
//                                                    if(DataHandlersUtil.datesCompare(issueDate, "2010-01-01") < 0){
//                                                        if(dataMap.size() > 0){
//                                                            ObjectMapper mapper = new ObjectMapper();
//                                                            String json = mapper.writeValueAsString(dataMap);
//                                                            journalInfoMap.put("data", json);
//                                                        }
//                                                        continue mainLoop;
//                                                    }
//                                                    try{
//                                                        if(null != dataMap && dataMap.size() > 0 && null != dataMap.get(volume) && null != dataMap.get(volume).get(issueText)){
//                                                            continue;
//                                                        }
//                                                        else{
//                                                            Map<String, String> issueMap = dataMap.get(volume);
//                                                            if(null == issueMap){
//                                                                issueMap = new HashMap<>();
//                                                                issueMap.put(issueText, issueDate);
//                                                                dataMap.put(volume, issueMap);
//                                                            }               
//                                                            else{
//                                                                issueMap.put(issueText, issueDate);
//                                                            }
//                                                            System.out.println("This is vol. "+volume+" and issue "+issueText+" and date "+issueDate);
//                                                        }
//                                                    }
//                                                    catch(Exception ex){
//                                                        System.out.println("Cannot add the pub date info into data map for vol. "+volume+" and issue "+issueText+" and date "+issueDate);
//                                                    }                                                       
//                                                }                                                    
//                                            }
//                                            }
//                                        }
//                                    }
//                                }
//
//                            }
//                        }
//                        if(dataMap.size() > 0){
//                            ObjectMapper mapper = new ObjectMapper();
//                            String json = mapper.writeValueAsString(dataMap);
//                            journalInfoMap.put("data", json);
//                        }
//                    }
//
//                }
//            }
//            if(kk > 100){
//                break;
//            }
//            kk++;
//        }
//    }
}
