/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.msofficedata;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    
    public static void main(String[] args) throws Exception
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        FileRouter router = (FileRouter) context.getBean("fileRouter");
        ExcelHandler excelHandler = (ExcelHandler) context.getBean("excelHandler");
        //PlosOneExcelData plosOneData = (PlosOneExcelData) context.getBean("plosOneExcelData");
        String path = FileUtil.getFilePathFromResources("plos_articles.xlsx");
        excelHandler.setFileName(path);
        excelHandler.readData();
        
        //String obj.getFileExtension();
    }
}
