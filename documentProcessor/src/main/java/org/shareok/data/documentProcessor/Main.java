/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    
    public static void main(String[] args) throws Exception
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("documentProcessorContext.xml");
        CsvHandler csv = (CsvHandler) context.getBean("csvHandler");
        String path = FileUtil.getFilePathFromResources("statusReport.csv");
        csv.setFileName(path);
        
        // ** test **
        HashMap colData = new HashMap();
        colData.put("0", "ok1");
        colData.put("23", "ok2");
        colData.put("34", "ok3");
        colData.put("45", "ok4");
        colData.put("56", "ok5");
        colData.put("67", "ok6");
        colData.put("78", "ok7");
        colData.put("89", "ok8");
        
        String colName = "Test";
        
        csv.addColumn(colName, 2, colData);
        
//        WordHandler wordHandler = (WordHandler) context.getBean("wordHandler");
//        //PlosOneExcelData plosOneData = (PlosOneExcelData) context.getBean("plosOneExcelData");
//        String path = FileUtil.getFilePathFromResources("test2.docx");
//        wordHandler.setFileName(path);
//        wordHandler.readData();
        
        //String obj.getFileExtension();
    }
}
