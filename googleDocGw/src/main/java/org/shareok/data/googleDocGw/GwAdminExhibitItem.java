/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.googleDocGw;

import java.util.HashMap;
import org.shareok.data.documentProcessor.CsvHandler;
import org.shareok.data.htmlrequest.HtmlRequest;

/**
 *
 * @author Tao Zhao
 */
public class GwAdminExhibitItem {
    CsvHandler csv;

    public CsvHandler getCsv() {
        return csv;
    }

    public void setCsv(CsvHandler csvHandler) {
        this.csv = csvHandler;
    }
    
    public static String getMmsidFromLinkToCatalog(String link) {
        String mmsId = "";
        
        String[] linkSplit = link.split("&term=");
        if(linkSplit instanceof String[] && linkSplit.length > 1){
            mmsId = linkSplit[linkSplit.length-1];
        }
        
        return mmsId;
    }
//    private HtmlRequest req;
//    
//
//    public HtmlRequest getReq() {
//        return req;
//    }
//
//    public void setReq(HtmlRequest req) {
//        this.req = req;
//    }
    
    
    
}
