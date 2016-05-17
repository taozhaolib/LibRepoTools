/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.documentProcessor;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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
        FileHandlerFactory.getFileHandlerByFileExtension("xlsx");
        ApplicationContext context = new ClassPathXmlApplicationContext("documentProcessorContext.xml");
        CsvHandler csv = (CsvHandler) context.getBean("csvHandler");
//        String path = FileUtil.getFilePathFromResources("/Users/zhao0677/Projects/sage-pub/sage-map.csv");
        csv.setFileName("/Users/zhao0677/Projects/law-library/pdf/load-test.csv");
        csv.deleteColumnByColumnName(new String[]{"Maps", "Fold-Out Charts", "collection name (repeatable)", "Special Collection (repeatable)", "rights (repeatable)", "media_type (repeatable)", "file_format (repeatable)"});
        
//        Map mapped = new HashMap<String, String>();
//        Map dataCsv = csv.getData();
//        Iterator it = dataCsv.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry pair = (Map.Entry)it.next();
//            String key = (String) pair.getKey();
//            String value = (String) pair.getValue();
//            if(null != key && null != value && !key.equals("") && !value.equals("")){
//                if(key.contains("[en_US]")){
//                    String row = key.split("-")[1];
//                    String doi = value.replace("/", ".");
//                    String id = dataCsv.get("dc.identifier.uri-"+row).toString().split("handle.net/")[1].split("/")[1];
//                    mapped.put(doi, id);
//                }
//            }
//        }
//        
//        int count = 0;
//        String outputPath = "/Users/zhao0677/Projects/sage-pub/output";
//        File output = new File(outputPath);
//        for (File fileEntry : output.listFiles()) {
//            count++;
//            if (fileEntry.isDirectory()) {
//                String articleFolderName = fileEntry.getName();
//                if(mapped.containsKey(articleFolderName)){
//                    //System.out.println("No. " + count + " : " + articleFolderName + " article has been found");
//                    File articleFolder = new File(outputPath + "/" + articleFolderName);
//                    for(File articleFile : articleFolder.listFiles()) {
//                        String fileName = articleFile.getName();
//                        if(fileName.contains(".pdf")){
//                            System.out.println(" The PDF file name has been changed from " + fileName + " " + mapped.get(articleFolderName).toString() + ".pdf");
//                            articleFile.renameTo(new File(outputPath + "/" + articleFolderName + "/" + mapped.get(articleFolderName).toString() + ".pdf"));
//                            //System.exit(0);
//                        }
//                    }
//                }
//                else{
//                    throw new Exception("The No. " + count + " : article " + articleFolderName + " is NOT found!");
//                }
//            } else {
//                System.out.println("No. " + count + " : " + fileEntry.getName() + " is NOT a folder!");
//            }
//        }
//        
//        // ** test **
//        HashMap colData = new HashMap();
//        colData.put("0", "ok1");
//        colData.put("23", "ok2");
//        colData.put("34", "ok3");
//        colData.put("45", "ok4");
//        colData.put("56", "ok5");
//        colData.put("67", "ok6");
//        colData.put("78", "ok7");
//        colData.put("89", "ok8");
//        
//        String colName = "Test";
//        
//        csv.addColumn(colName, 2, colData);
        
//        WordHandler wordHandler = (WordHandler) context.getBean("wordHandler");
//        //PlosOneExcelData plosOneData = (PlosOneExcelData) context.getBean("plosOneExcelData");
//        String path = FileUtil.getFilePathFromResources("test2.docx");
//        wordHandler.setFileName(path);
//        wordHandler.readData();
        
        //String obj.getFileExtension();
    }
}
