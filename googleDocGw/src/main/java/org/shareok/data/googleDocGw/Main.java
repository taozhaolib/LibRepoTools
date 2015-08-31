/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.googleDocGw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.shareok.data.documentProcessor.FileUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("googleDocGwContext.xml");
        GwAdminExhibitItem item = (GwAdminExhibitItem) context.getBean("gwAdminExhibitItem");
        
//        String path = FileUtil.getFilePathFromResources("exhibitItems.csv");
//        item.csv.setFileName(path);
//        item.csv.readData();
//        HashMap statusReport = item.csv.getData();
//        
//        Iterator it = statusReport.entrySet().iterator();
//        Map mmsidVal = new HashMap();
//        while(it.hasNext()){
//            Map.Entry pair = (Map.Entry)it.next();
//            String key = (String) pair.getKey();
//            String value = (String) pair.getValue();
//            if(key.contains("Link to Catalog") && null != value && !value.equals("")){
//                value = value.split("&term=")[1];
//                String[] keyInfo = key.split("-");
//                String rowNum = keyInfo[keyInfo.length-1];
//                String mmsid = (String)statusReport.get("MMSID-"+rowNum);
//                if(null == mmsid || mmsid.equals("")){
//                    mmsidVal.put("mmsid-"+rowNum, value);
//                }
//            }
//        }
//        
//        it = mmsidVal.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry pair = (Map.Entry)it.next();
//            String key = (String) pair.getKey();
//            String value = (String) pair.getValue();
//            if(null != value && !value.equals("")){
//                statusReport.put(key, value);
//            }
//        }
//        
//        item.csv.setData(statusReport);
//        item.csv.outputData();

        
//        path = FileUtil.getFilePathFromResources("GWDigiList.csv");
//        item.csv.setFileName(path);
//        item.csv.readData();
//        HashMap GWDigiList = item.csv.getData();
//        
//        path = FileUtil.getFilePathFromResources("exhibitItems.csv");
//        item.csv.setFileName(path);
//        item.csv.readData();
//        HashMap exhibitItems = item.csv.getData();
//        
        String path = FileUtil.getFilePathFromResources("statusReport-copy.csv");
        item.csv.setFileName(path);
        item.csv.readData();
        Map exhibitData = item.csv.getDataMapByColumns("ALMA ID", "MMSID");
        
        String statusReportPath = FileUtil.getFilePathFromResources("exhibitItems-copy.csv");
        item.csv.setFileName(statusReportPath);
        item.csv.readData();
        HashMap statusReport = item.csv.getData(); 
        
        String[] map = item.csv.getFileHeadMapping();
        ArrayList<String> mapList = new ArrayList();
        for(String col : map){
            mapList.add(col);
        }
        mapList.add(3, "MMSID");
        map = mapList.toArray(new String[mapList.size()]);
        item.csv.setFileHeadMapping(map);
       
        Iterator it = exhibitData.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            String key = (String) pair.getKey();
            String value = (String) pair.getValue();
            if(null != key && null != value && !key.equals("") && !value.equals("")){
                for(int i = 0; i < item.csv.getRecordCount(); i++){
                    String match_val = (String)statusReport.get("ALMA # (Unique ID)-"+i);
                    //System.out.print("\nFor lin "+i+", matching val ="+match_val+"for key="+key);
                    if(key.equals(match_val)){
                        statusReport.put("MMSID-"+i, value);
                        //System.out.print("\n For alma id = "+value+" was updated at line "+i+"with value = "+value +"\n");
                    }
                }
            }
        }
        
        item.csv.setData(statusReport);
        item.csv.outputData();
        System.out.print("OK");
    }
}
