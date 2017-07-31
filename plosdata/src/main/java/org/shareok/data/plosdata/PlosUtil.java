/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosdata;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shareok.data.datahandlers.exceptions.InvalidDoiException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class PlosUtil {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PlosDoiDataImpl.class);

    /**
     * api search example: http://api.plos.org/search?q=author_affiliate%3A%22University+of%20Oklahoma%22 
     * AND publication_date:[2016-01-01T00:00:00Z%20TO%202016-12-31T23:59:59Z]&rows=500
     * &fl=affiliate,doi,author,volume,issue,title,journal,publication_date&api_key=mC8TEAzqwfvDWs4eamFh
     * 
    **/

    public static final String API_SEARCH_FACETS = "affiliate,doi,author,volume,issue,title,journal,publication_date";
    public static final String API_SEARCH_PREFIX = "http://api.plos.org/search?q=";
    public static final String API_SEARCH_ROW = "500";
    public static final String API_FULLTEXT_PDF_PREFIX = "http://dx.plos.org/";
    public static final String URL_PDF_FULLTEXT_PREIFX = "http://journals.plos.org/";
    public static final String DOI_PREFIX = "http://dx.doi.org/";    
    public static final String URL_FULLTEXT_PDF_PREFIX = "http:///dx.plos.org/";
    
    public static final String URL_FULLTEXT_PREFIX_PONE = "http://www.plosone.org/article/info%3Adoi%2F";
    public static final String URL_FULLTEXT_PREFIX_PBIO = "http://www.plosbiology.org/article/info%3Adoi%2F";
    public static final String URL_FULLTEXT_PREFIX_PGEN = "http://www.plosgenetics.org/article/info%3Adoi%2F";
    public static final String URL_FULLTEXT_PREFIX_PMED = "http://www.plosmedicine.org/article/info%3Adoi%2F";
    public static final String URL_FULLTEXT_PREFIX_PCBI = "http://www.ploscompbiol.org/article/info%3Adoi%2F";
    public static final String URL_FULLTEXT_PREFIX_PPAT = "http://www.plospathogens.org/article/info%3Adoi%2F";
    public static final String URL_FULLTEXT_PREFIX_PNTD = "http://www.plosntds.org/article/info%3Adoi%2F";
    
    public static final String PEERREVIEWNOTES_PONE = "http://www.plosone.org/static/editorial#peer";   
    public static final String PEERREVIEWNOTES_PBIO = "http://www.plosbiology.org/static/editorial#peer";   
    public static final String PEERREVIEWNOTES_PGEN = "http://www.plosgenetics.org/static/editorial#peer";   
    public static final String PEERREVIEWNOTES_PMED = "http://www.plosmedicine.org/static/editorial#peer";   
    public static final String PEERREVIEWNOTES_PCBI = "http://www.ploscompbiol.org/static/editorial#peer";   
    public static final String PEERREVIEWNOTES_PPAT = "http://www.plospathogens.org/static/editorial#peer";   
    public static final String PEERREVIEWNOTES_PNTD = "http://www.plosntds.org/static/editorial#peer";   
    
    public static Map<String, String> PLOS_PDF_DOWNLINK_PUB_MAP = new HashMap<>();
    static {
        PLOS_PDF_DOWNLINK_PUB_MAP.put("pmed", "plosmedicine");
        PLOS_PDF_DOWNLINK_PUB_MAP.put("pbio", "plosbiology");
        PLOS_PDF_DOWNLINK_PUB_MAP.put("pcbi", "ploscompbiol");
        PLOS_PDF_DOWNLINK_PUB_MAP.put("pgen", "plosgenetics");
        PLOS_PDF_DOWNLINK_PUB_MAP.put("pntd", "plosntds");
        PLOS_PDF_DOWNLINK_PUB_MAP.put("ppat", "plospathogens");
        PLOS_PDF_DOWNLINK_PUB_MAP.put("pone", "plosone");
    }
    
    public enum JournalType {        
        PLOSONE, PLOSBIO, PLOSGEN, PLOSMED, PLOSCBI, PLOSPAT, PLOSNTD
    }
    
    //public static final String PLOSONE_DOWNLOAD_PATH = "/Users/zhao0677/Projects/plosOne/importData";
    //http://journals.plos.org/plosmedicine/article/file?id=10.1371/journal.pmed.1002221&type=printable
//    http://journals.plos.org/plosbiology/article/file?id=10.1371/journal.pbio.2000942&type=printable
//    http://journals.plos.org/ploscompbiol/article/file?id=10.1371/journal.pcbi.1005290&type=printable
//    http://journals.plos.org/plosgenetics/article/file?id=10.1371/journal.pgen.1006505&type=printable
//    http://journals.plos.org/plosntds/article/file?id=10.1371/journal.pntd.0005132&type=printable
//    http://journals.plos.org/plospathogens/article/file?id=10.1371/journal.ppat.1006077&type=printable
//    http://journals.plos.org/plosone/article/file?id=10.1371/journal.pone.0167412&type=printable
    
    public static ApplicationContext getPlosContext() {
        ApplicationContext context = new ClassPathXmlApplicationContext("plosContext.xml");
        return context;
    }
        
    /**
     * 
     * @param doiInfo : in a form of 10.1371/journal.pone.0041479
     */
    public static String getPlosPDFUrl(String doiInfo) {
        String[] doiInfoArr = doiInfo.split("/");
        
        try {
            if(null == doiInfoArr && doiInfoArr.length != 2){
                throw new InvalidDoiException("The doi "+doiInfo+" is not valid!");
            }
//            doiInfo = doiInfoArr[1];
//            doiInfoArr = doiInfo.split("\\.");
            String pubType = (String)PLOS_PDF_DOWNLINK_PUB_MAP.get(doiInfoArr[1].split("\\.")[1]);
            return URL_PDF_FULLTEXT_PREIFX + pubType + "/article/file?id=" + doiInfo + "&type=printable";
        } catch (InvalidDoiException ex) {
            logger.error(ex);            
        }
        return null;
    }
    
    public static String getPlosAck(String html) {
            
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
    
    public static String getPlosCitation(String html) {
        
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
    
    /**
     * 
     * @param html : The string of the web page source
     * @return author contribution statement
     */
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
    
    /**
     * 
     * @param html : The string of the web page source
     * @return acknowledge statement
     */
    public static String[] getSubjects(String html) {
        List<String> subjectsList = new ArrayList<>();
        
        Document doc = Jsoup.parse(html.toString());
        Elements subjectListDiv = doc.select("div[class=subject-areas-container]");
        if(null != subjectListDiv && !subjectListDiv.isEmpty()){
            Element subjectList = subjectListDiv.first().child(1);
            if(null != subjectList){
                Elements lis = subjectList.select("li");
                if(null != lis && lis.size() > 0){
                    for (Element li : lis){
                        Element link = li.child(0);
                        subjectsList.add(link.text());
                    }
                }
            }
        }
        if(subjectsList.size() > 0){
            return subjectsList.toArray(new String[subjectsList.size()]);
        }
        else{
            return null;
        }
    }
    
    /**
     * For some correspondences, there are no metadata about article title, <br>
     * instead, they is a title tag
     * @param html : The string of the web page source
     * @return title
     */
    public static String getTitleFromHtml(String html) {
        String title = "";
        
        Document doc = Jsoup.parse(html.toString());
        Elements titleElements = doc.select("title");
        if(null != titleElements && titleElements.size() > 0){
            title = titleElements.get(0).text();
        }
        return title;
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
