/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.lawlibrary;

import java.io.File;
import java.util.List;
import org.shareok.data.documentProcessor.FileUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("lawLibContext.xml");
        LawLibDataHandlerImpl handle = (LawLibDataHandlerImpl) context.getBean("lawLibDataHandlerImpl");
        handle.getPdfFileListFromTextFile("/Users/zhao0677/Projects/law-library/test-pdf/unmatchedPdfFiles.txt");
        handle.setOutputFilePath("/Users/zhao0677/Projects/law-library/test-pdf");
        handle.setInputFilePath("/Users/zhao0677/Projects/law-library/test1.csv");
        handle.readSourceData();
//        for(String file : handle.getPdfFileList()){
//            String matchedFile = handle.matchSerialAFiles(file);
//            if(!file.equals(matchedFile)){
//                handle.getMatchedPdfFileList().add(matchedFile);
//            }
//        }
//        FileUtil.outputStringToFile(String.join("\n", handle.getMatchedPdfFileList()), new File(handle.getOutputFilePath()).getPath() + File.separator + "matchedSerialPdfFiles.txt");
//        handle.setInputFilePath("/Users/zhao0677/Projects/law-library/test1.csv");
//        handle.setOutputFilePath("/Users/zhao0677/Projects/law-library/test-pdf");
//        List matched = handle.getMatchedPdfFileList();
//        matched.add("1");
//        matched.add("2");
//        String path = new File(handle.getOutputFilePath()).getParent() + File.separator + "matchedPdfFiles.txt";
//        FileUtil.outputStringToFile(String.join("\n", matched), path);
//        handle.readSourceData();
        System.out.print("ok");
    }
}
