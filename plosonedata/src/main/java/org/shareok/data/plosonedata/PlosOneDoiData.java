/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosonedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.shareok.data.msofficedata.ExcelHandler;
import org.shareok.data.msofficedata.FileUtil;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Tao Zhao
 */
public class PlosOneDoiData implements ExcelData {
    
    private HashMap<String,String[]> data;
    private ExcelHandler excelHandler;
    private ArrayList<String> doiData;

    public HashMap<String, String[]> getData() {
        return data;
    }

    public ExcelHandler getExcelHandler() {
        return excelHandler;
    }

    /**
     *
     * @param data
     */
    public void setData(HashMap<String, String[]> data) {
        this.data = data;
    }

    public void setExcelHandler(ExcelHandler excelHandler) {
        this.excelHandler = excelHandler;
    }

    public ArrayList<String> getDoiData() {
        return doiData;
    }
    
    @Override
    public void addData(int row, int col, Object data){
        String key = Integer.toString(row) + "-" + Integer.toString(col);
        HashMap dataVal = this.getData();
        dataVal.put(key, data);
    }

    public void setDoiData(ArrayList<String> doiData) {
        this.doiData = doiData;
    }
    
    @Override
    public void printData() {
        
    }
    
    @Override
    public void convertHashMapDataToDoi(HashMap mapData) throws Exception {
        Set keys = mapData.keySet();
        Iterator it = keys.iterator();

        ArrayList <String> doiList = new ArrayList<>();
        while(it.hasNext()){
            String key = (String)it.next();
            String value = (String)mapData.get(key);
            // the values is composed of "val--datatype": for example, Tom--Str or 0.50--num
            String[] values = value.split("--");
            if(null == values || values.length != 2)
                continue;
            String type = values[1];
            value = values[0];
            String[] rowCol = key.split("-");
            if(null == rowCol || rowCol.length != 2)
                throw new Exception("The row and column are not specifid!");
            int row = Integer.parseInt(rowCol[0]);
            int col = Integer.parseInt(rowCol[1]);
            if(col == 3 && value.contains("journal.pone")){
                int index = value.toLowerCase().indexOf("plos one");
                if(index == -1)
                    continue;
                String doiVal = value.substring(index);
                Pattern pattern = Pattern.compile("(e)(\\d{5})");
                Matcher matcher = pattern.matcher(doiVal);
                if (matcher.find())
                {
                    String[] doiInfo = doiVal.split(":");
                    if(doiInfo.length != 3)
                        continue;
                    doiVal = doiInfo[2];
                    doiList.add(doiVal);
                    //System.out.println(matcher.group(1) + " - " + matcher.group(2));
                }
            }
        }
        setDoiData(doiList);
    }
    
    public void getMetaData() throws Exception {
        ArrayList<String> doiList = getDoiData();
        if(!doiList.isEmpty()) {
            PlosOneRequest req = (PlosOneRequest)PlosOneUtil.getPlosOneContext().getBean("plosOneRequest");
            for(String doi : doiList){
                //PlosOneUtil.downLoadFullPDF(doi, "path");
                String[] doiArr = doi.split(":");
                doi = doiArr[0];
                String doiData = req.getMetaDataByApi(doi);
                Document doc = FileUtil.loadXMLFromString(doiData);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("str");

                int length = nList.getLength();

                if(length == 0){
                    continue;
                }

                List<String> dataList = new ArrayList<>();
                for (int temp = 0; temp < length; temp++) {

                        Node nNode = nList.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) nNode;
                                if(eElement.getAttribute("name").equals("title_display"))
                                    dataList.add(eElement.getTextContent());
                                //System.out.println("file type : " + eElement.getTextContent() + "\n");

                        }
                }
                System.out.println(dataList.toString());System.exit(0);
                //System.out.print(doiData + "\n\n");
            }
        }System.exit(0);
    }
    
    @Override
    public void importData(String fileName) {

        String path = FileUtil.getFilePathFromResources(fileName);
        excelHandler.setFileName(path);
        try {
            excelHandler.readData();
            HashMap mapData = excelHandler.getData();
            convertHashMapDataToDoi(mapData);
        } catch (Exception ex) {
            Logger.getLogger(PlosOneDoiData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void exportXmlData(String filePath){
        
    }
}
