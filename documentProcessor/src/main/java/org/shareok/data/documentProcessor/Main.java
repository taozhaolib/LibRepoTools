/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    
    public static void main(String[] args) throws Exception
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("documentProcessor.xml");
        FileRouter router = (FileRouter) context.getBean("fileRouter");
        WordHandler wordHandler = (WordHandler) context.getBean("wordHandler");
        //PlosOneExcelData plosOneData = (PlosOneExcelData) context.getBean("plosOneExcelData");
        String path = FileUtil.getFilePathFromResources("test2.docx");
        wordHandler.setFileName(path);
        wordHandler.readData();
        
        //String obj.getFileExtension();
    }
}
