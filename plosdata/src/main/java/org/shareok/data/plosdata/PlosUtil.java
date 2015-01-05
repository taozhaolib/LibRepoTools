/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosdata;

import java.io.PrintWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class PlosUtil {
    public static final String API_KEY = "mC8TEAzqwfvDWs4eamFh";
    public static final String API_SEARCH_PREFIX = "http://api.plos.org/search?q=";
    public static final String API_FULLTEXT_PDF_PREFIX = "http://dx.plos.org/";
    public static final String URL_PDF_FULLTEXT_PREIFX = "http://dx.plos.org/";
    public static final String DOI_PREFIX = "http://dx.doi.org/";
    public static final String URL_FULLTEXT_PREFIX = "http://www.plosone.org/article/info%3Adoi%2F";
    public static final String URL_FULLTEXT_PDF_PREFIX = "http:///dx.plos.org/";
    //public static final String PLOSONE_DOWNLOAD_PATH = "/Users/zhao0677/Projects/plosOne/importData";
    //"http://dx.plos.org/10.1371/journal.pone.0041479.pdf";
    //public static final String URL_FULLTEXT_PREFIX = "http://www.plosone.org/article/info%3Adoi%2F10.1371%2Fjournal.pone.0115508";
    
    public static ApplicationContext getPlosOneContext() {
        ApplicationContext context = new ClassPathXmlApplicationContext("plosContext.xml");
        return context;
    }
    
    /**
     * 
     * @param doiInfo : in a form of 10.1371/journal.pone.0041479
     * @param filePath 
     */
    public static void downLoadFullPDF(String doiInfo, String filePath) {
        String urlPdf = URL_PDF_FULLTEXT_PREIFX + doiInfo + ".pdf";
    }
    
    public static String getPlosOneAck(String html) {
            
        String ack = "";
        Document doc = Jsoup.parse(html.toString());
        Elements ackLinks = doc.select("a[id=ack]");
        if(!ackLinks.isEmpty()){
            Element ackDiv = ackLinks.first().parent();
            if(null != ackDiv){
                Elements ackParagraphs = ackDiv.select("p");
                if(!ackParagraphs.isEmpty()){
                    for(Element element : ackParagraphs) {
                        if(element.hasText())
                            ack += element.text();
                    }
                }
                //System.out.println("the ack = "+ack+"\n\n");
            }
        }            

        return ack;
    }
    
    public static String getPlosOneCitation(String html) {
        
        String citation = "";
        
        Document doc = Jsoup.parse(html.toString());
        Elements articleInfoDiv = doc.select("div[class=articleinfo]");
        if(!articleInfoDiv.isEmpty()){
            Element citationParagraph = articleInfoDiv.first().child(0);
            if(null != citationParagraph){
                citation = citationParagraph.text().replace("Citation:", "");
                //System.out.println("the citation = "+citation+"\n\n");
            }
        }            

        return citation;
    }
    
    public static String getAuthorContributions(String html) {
        String contributions = "";
        
        Document doc = Jsoup.parse(html.toString());
        Elements articleInfoDiv = doc.select("div[class=contributions]");
        if(!articleInfoDiv.isEmpty()){
            Element contributionsParagraph = articleInfoDiv.first().child(2);
            if(null != contributionsParagraph){
                contributions = contributionsParagraph.text();
                //System.out.println("the contributions = "+contributions+"\n\n");System.exit(0);
            }
        }            

        return contributions;
    }
    
    public static void createContentFile(String fileName, String content){
        try{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(content);
            writer.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
}
