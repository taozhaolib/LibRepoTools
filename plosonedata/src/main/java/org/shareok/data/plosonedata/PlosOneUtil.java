/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosonedata;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class PlosOneUtil {
    public static final String API_KEY = "mC8TEAzqwfvDWs4eamFh";
    public static final String API_SEARCH_PREFIX = "http://api.plos.org/search?q=";
    public static final String API_FULLTEXT_PDF_PREFIX = "http://dx.plos.org/";
    public static final String URL_PDF_FULLTEXT_PREIFX = "http://dx.plos.org/";
    public static final String URL_FULLTEXT_PREFIX = "http://www.plosone.org/article/info";
    //public static final String URL_FULLTEXT_PREFIX = "http://www.plosone.org/article/info%3Adoi%2F10.1371%2Fjournal.pone.0115508";
    
    public static ApplicationContext getPlosOneContext() {
        ApplicationContext context = new ClassPathXmlApplicationContext("plosOneContext.xml");
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
}
