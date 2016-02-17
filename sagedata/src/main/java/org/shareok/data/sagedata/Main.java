package org.shareok.data.sagedata;

import java.util.Arrays;
import org.shareok.data.documentProcessor.FileHandler;
import org.shareok.data.documentProcessor.FileUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("sageDataContext.xml");
        SageSourceDataHandlerImpl ssd = (SageSourceDataHandlerImpl) context.getBean("sageSourceDataHandlerImpl");
        String path = FileUtil.getFilePathFromResources("sagedata.xlsx");///Users/zhao0677/Projects/shareokdata/sagedata/target/classes/sagedata.xlsx
        ssd.setSourceFilePath(path);
        String[] pathInfo = path.split("/");
        pathInfo[pathInfo.length-1] = "output";
        path = String.join("/", pathInfo);
        System.out.print(path);
        ssd.setOutputFilePath(path);
        ssd.readSourceData();
        //ssd.readSourceData(FileUtil.getFilePathFromResources("sagedata.xlsx"));
        ssd.processSourceData();
        ssd.outputMetaData();
    }
}
