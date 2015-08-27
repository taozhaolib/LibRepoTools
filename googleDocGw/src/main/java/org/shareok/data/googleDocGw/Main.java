/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.googleDocGw;

import java.util.HashMap;
import java.util.Iterator;
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
        
//        String path = FileUtil.getFilePathFromResources("statusReport.csv");
//        item.csvHandler.setFileName(path);
//        item.csvHandler.readData();
//        HashMap statusReport = item.csvHandler.getData();
//        
//        path = FileUtil.getFilePathFromResources("GWDigiList.csv");
//        item.csvHandler.setFileName(path);
//        item.csvHandler.readData();
//        HashMap GWDigiList = item.csvHandler.getData();
//        
//        path = FileUtil.getFilePathFromResources("exhibitItems.csv");
//        item.csvHandler.setFileName(path);
//        item.csvHandler.readData();
//        HashMap exhibitItems = item.csvHandler.getData();
//        
//        Iterator it = exhibitItems.entrySet().iterator();
//        while(it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            it.remove();
//        }
    }
}
