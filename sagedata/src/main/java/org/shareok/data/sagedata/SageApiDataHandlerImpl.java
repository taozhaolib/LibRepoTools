/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shareok.data.config.DataUtil;
import org.shareok.data.datahandlers.DataHandlersUtil;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.htmlrequest.exceptions.ErrorHandlingResponseException;
import org.shareok.data.htmlrequest.exceptions.ErrorResponseCodeException;
import org.shareok.data.sagedata.exceptions.EmptyArticleTypeInfoException;
import org.shareok.data.sagedata.exceptions.NoSageSearchCurrentArticleIndexException;
import org.shareok.data.sagedata.exceptions.NoSageSearchTotalRecordsException;
import org.shareok.data.sagedata.exceptions.NullResponseBySageSearchRequestException;

/**
 *
 * @author Tao Zhao
 */
public class SageApiDataHandlerImpl implements SageApiDataHandler {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SageApiDataHandlerImpl.class);
    
    @Override
    public String getApiResponseByDatesAffiliate(String startDate, String endDate, String affiliate) {
        String json;
        String query = getApiQuery(startDate, endDate, affiliate);
        System.out.println("query = " + query);
        List<Map<String, String>>articleList = new ArrayList<>();
        int currentIndex = -1;
        int total = -1;
        int page = 0;
        try {
            while(currentIndex < total || total == -1){
                if(currentIndex != -1){
                    query = getApiQuery(startDate, endDate, affiliate, String.valueOf(page));
                }
                Document doc = getApiResponseByQuery(query);
                if(null == doc){
                    try {
                        throw new NullResponseBySageSearchRequestException("Get null response with query = "+query);
                    } catch (NullResponseBySageSearchRequestException ex) {
                        logger.error(ex);
                        continue;
                    }
                }
                if(total == -1){
                    total = getTotalArticlesFound(doc);
                }
                currentIndex = getCurrentArticleIndex(doc);            
                getArticleInfoFromDoc(articleList, doc);
                page++;
            }
            
            int count = 0;
            List<Integer> removeList = new ArrayList<>();
            for(Map<String, String> articleInfo : articleList){
                String pubDate = articleInfo.get("publication date");
                if(DataHandlersUtil.datesCompare(startDate, pubDate) > 0 || DataHandlersUtil.datesCompare(pubDate, endDate) > 0){
                    removeList.add(count);
                }
                count++;
            }
            
            int length = removeList.size();
            for(int i = length - 1; i >= 0; i--){
                articleList.remove((int)removeList.get(i));
            }
            
            json = DataUtil.getJsonFromListOfMap(articleList);
        } catch (ErrorResponseCodeException | ErrorHandlingResponseException ex) {
            logger.error("Cannot get response sage data after http get request wiht query = "+query, ex);
            return null;
        } catch (NoSageSearchTotalRecordsException ex) {
            logger.error("Cannot get total records!", ex);
            return null;
        } catch (NoSageSearchCurrentArticleIndexException ex) {
            logger.error("Cannot get index of current article!", ex);
            return null;
        }
        
        return json;
    }

    @Override
    public String outputResponse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private String getApiQuery(String startDate, String endDate, String affiliate){
        return getApiQuery(startDate, endDate, affiliate, "0");
    }
    
    private String getApiQuery(String startDate, String endDate, String affiliate, String startPage){
        
        try {
            String startYear = DataHandlersUtil.getYearFromSimpleDateString(startDate);
            String endYear = DataHandlersUtil.getYearFromSimpleDateString(endDate);
            
            return SageDataUtil.API_SEARCH_PREFIX + "field1=Affiliation&text1=\"" + URLEncoder.encode(affiliate, "UTF-8")+ "\"&field2=AllField&text2=&Ppub=&Ppub=&AfterYear=" + startYear + "&BeforeYear=" + endYear + "&access=&pageSize=20&startPage=" + startPage + "&";
        } catch (Exception ex) {
            logger.error("Cannot encode the query parameters!", ex);
        }
        return null;
    }
    
    private Document getApiResponseByQuery(String query) throws ErrorResponseCodeException, ErrorHandlingResponseException{

        Document doc = null;
        try {
            doc = Jsoup.connect(query)
                    .data("query", "Java")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .cookie("auth", "token")
                    .timeout(300000)
                    .get();
        } catch (IOException ex) {
            logger.error("Cannot get the SAGE pub search results by the query: "+query, ex);
        }
        return doc;
    }
    
    private int getTotalArticlesFound(Document doc) throws NoSageSearchTotalRecordsException{
        int total = -1;
        Elements eles = doc.select("span.emphasis");
        if(null != eles && eles.size() > 0){
            Element emphasisSpan = eles.get(0);
            Element emphasisSpanParent = emphasisSpan.parent();
            String totalStr = emphasisSpanParent.text().trim().split(" of ")[1];
            total = Integer.valueOf(totalStr);
        }
        else{
            throw new NoSageSearchTotalRecordsException("Cannot find the total records information!");
        }
        return total;
    }
    
    private int getCurrentArticleIndex(Document doc) throws NoSageSearchCurrentArticleIndexException{
        int index = -1;
        Elements eles = doc.select("span.emphasis");
        if(null != eles && eles.size() > 0){
            Element emphasisSpan = eles.get(0);
            Element emphasisSpanParent = emphasisSpan.parent();
            String indexStr = (emphasisSpanParent.text().trim().split(" of ")[0]).split(" – ")[1];
            index = Integer.valueOf(indexStr);
        }
        else{
            throw new NoSageSearchCurrentArticleIndexException("Cannot find the total records information!");
        }
        return index;
    }
    
    private void getArticleInfoFromDoc(List<Map<String, String>> articleList, Document doc){
        Elements articleElements = doc.select("article.searchResultItem");
        if(articleElements.size() > 0){
            for(Element article : articleElements){ 
                try{
                    String articleType = article.select("span.ArticleType").get(0).text();
                    if(null == articleType || DocumentProcessorUtil.isEmptyString(articleType)){
                        try {
                            throw new EmptyArticleTypeInfoException("Cannot get article type information!");
                        } catch (EmptyArticleTypeInfoException ex) {
                            logger.error(ex);
                            continue;
                        }
                    }
                    boolean fullAccess = false;
                    Elements accessIcons = article.select("img.freeAccess");
                    if(null != accessIcons && accessIcons.size() > 0){
                        Element accessIconElement = accessIcons.get(0);
                        if(accessIconElement.attr("title").equals("Free Access")){
                            fullAccess = true;
                        }
                    }
                    else{
                        accessIcons = article.select("img.fullAccess");
                        if(null != accessIcons && accessIcons.size() > 0){
                            Element accessIconElement = accessIcons.get(0);
                            if(accessIconElement.attr("title").equals("Full Access")){
                                fullAccess = true;
                            }
                        }
                    }
                    if(fullAccess == true){
                        Map articleInfoMap = new HashMap<>();
                        Element titleDiv = article.select("div.art_title").get(0);
                        Element titleLink = titleDiv.select("a.ref").get(0);
                        articleInfoMap.put("title", titleLink.text().trim());

                        Element tocDeliverFormatsLinks = article.select("div.tocDeliverFormatsLinks").get(0);
                        Elements absLinkElements = tocDeliverFormatsLinks.select("a.abstract");
                        String abstractLink = "";
                        if(absLinkElements.size()>0){
                            Element absElement = absLinkElements.get(0);
                            abstractLink = SageDataUtil.SAGE_HTTP_PREFIX + absElement.attr("href").trim();
                            articleInfoMap.put("abstract link", abstractLink);
                        }
                        else{
                            articleInfoMap.put("abstract link", "");
                        }

                        Element pdfLink = tocDeliverFormatsLinks.select("a.pdf").get(0);
                        String pdfLinkStr = SageDataUtil.SAGE_HTTP_PREFIX + pdfLink.attr("href").trim();
                        articleInfoMap.put("PDF link", pdfLinkStr);
                        try{
                            articleInfoMap.put("doi", pdfLinkStr.split("pdf/")[1]);
                        }
                        catch(Exception ex){
                            try{
                                articleInfoMap.put("doi", abstractLink.split("doi/abs/")[1]);
                            }
                            catch(Exception ex2){
                                articleInfoMap.put("doi", "");
                            }
                        }

                        Element pubDate = article.select("span.maintextleft").get(0);
                        String pubDateStr = pubDate.text().split("Published ")[1].trim().split("\\.")[0];
                        articleInfoMap.put("publication date", DataHandlersUtil.convertFullMonthDateStringFormat(pubDateStr));
                        
                        Element journal = article.select("span.journal-title").get(0);
                        String journalStr = journal.text();
                        articleInfoMap.put("journal", journalStr);
                        
                        Elements authorSpans = article.select("div.author").get(0).select("span.contribDegrees");
                        String authorsStr = "";
                        for(Element authorSpan : authorSpans){
                            authorsStr += authorSpan.select("a.entryAuthor").get(0).text() + ";";
                        }
                        articleInfoMap.put("author", authorsStr.substring(0, authorsStr.length()-1));

                        articleList.add(articleInfoMap);
                    }      
                }
                catch(Exception ex){
                    logger.error("Cannot interpret the SAGE article information!", ex);
                }
            }
        }
    }
}
