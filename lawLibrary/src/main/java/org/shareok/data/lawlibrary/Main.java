/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.lawlibrary;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.shareok.data.documentProcessor.CsvHandler;
import org.shareok.data.documentProcessor.FileUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){

//            CsvHandler csv = new CsvHandler();
//            LawLibDataHandlerImpl handle = new LawLibDataHandlerImpl();//(LawLibDataHandlerImpl) context.getBean("lawLibDataHandlerImpl");
//            handle.setCsv(csv);
//            handle.setInputFilePath("/Users/zhao0677/Projects/law-library/test1.csv");
//            handle.setOutputFilePath("/Users/zhao0677/Projects/law-library/SAF4");
//            handle.readSourceData();
        
            for(int count = 6; count < 10; count++){
                CsvHandler csv = new CsvHandler();
                String countStr = String.valueOf(count);
                LawLibDataHandlerImpl handle = new LawLibDataHandlerImpl();//(LawLibDataHandlerImpl) context.getBean("lawLibDataHandlerImpl");
                handle.setCsv(csv);
                handle.setInputFilePath("/Users/zhao0677/Projects/law-library/test1.csv");
                handle.setOutputFilePath("/Volumes/TOSHIBA EXT 37/law-lib-saf/SAF"+countStr);
                handle.readSourceData();
            }
        
//        for(int count = 3; count < 5; count++){
//            String countStr = String.valueOf(count);
//            LawLibDataHandlerImpl handle = new LawLibDataHandlerImpl();//(LawLibDataHandlerImpl) context.getBean("lawLibDataHandlerImpl");
//            handle.setOutputFilePath("/Users/zhao0677/Projects/law-library/SAF"+countStr);
//            handle.outputSafPackage();
//        }
        
        
//        for(int count = 20; count < 25; count++){
//            CsvHandler csv = new CsvHandler();
//            String countStr = String.valueOf(count);
//            LawLibDataHandlerImpl handle = new LawLibDataHandlerImpl();//(LawLibDataHandlerImpl) context.getBean("lawLibDataHandlerImpl");
//            handle.setCsv(csv);
//            handle.setInputFilePath("/Users/zhao0677/Projects/law-library/test1.csv");
//            handle.setOutputFilePath("/Users/zhao0677/Projects/law-library/SAF"+countStr);
//            handle.outputSafPackage();
//            handle.readSourceData();
//        }
        
//        List matched = handle.getMatchedPdfFileList();
//        matched.add("1");
//        matched.add("2");
//        String path = new File(handle.getOutputFilePath()).getParent() + File.separator + "matchedPdfFiles.txt";
//        FileUtil.outputStringToFile(String.join("\n", matched), path);
        
        System.out.print("ok");
    }
}
