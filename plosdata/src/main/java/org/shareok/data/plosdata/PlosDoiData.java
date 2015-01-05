/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosdata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.shareok.data.htmlrequest.HtmlParser;
import org.shareok.data.msofficedata.ExcelHandler;
import org.shareok.data.msofficedata.FileUtil;

/**
 *
 * @author Tao Zhao
 */
public class PlosDoiData implements ExcelData {
    
    private HashMap<String,String[]> data;
    private ExcelHandler excelHandler;
    private ArrayList<String> doiData;
    private ArrayList<PlosData> plosDataList;

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

    public ArrayList<PlosData> getPlosDataList() {
        return plosDataList;
    }

    public void setPlosDataList(ArrayList<PlosData> plosDataList) {
        this.plosDataList = plosDataList;
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
            if(col == 3 && value.contains("/journal.p")){
                int index = value.toLowerCase().indexOf("plos one");
                if(index == -1)
                    continue;
                String doiVal = value.substring(index);
                Pattern pattern = Pattern.compile("(e)(\\d{3,5})");
                Matcher matcher = pattern.matcher(doiVal);
                if (matcher.find())
                {
                    String[] doiInfo = doiVal.split(":");
                    if(doiInfo.length != 3)
                        continue;
                    String isPartOfSeries = doiInfo[0]+":"+matcher.group(1)+matcher.group(2);
                    doiVal = isPartOfSeries + "---" + doiInfo[2];
                    doiList.add(doiVal);
                    //System.out.println("Matcher find the string for "+doiInfo[0]+"!!!  \n");//System.out.println(matcher.group(1) + " - " + matcher.group(2));
                }
                else{
                    System.out.println("Matcher cannot find the string for "+doiVal+"!!!  \n");
                }
            }
        }//System.exit(0);
        setDoiData(doiList);
    }
    
    
    public void getMetaData() throws Exception {
        ArrayList<String> doiList = getDoiData();
        if(!doiList.isEmpty()) {
            
            PlosRequest req = (PlosRequest)PlosUtil.getPlosOneContext().getBean("plosRequest");
            PlosData plosData = (PlosData)PlosUtil.getPlosOneContext().getBean("plosData");
            
            for(String doi : doiList){
                String[] valArray = doi.split("---");
                String isPartOfSeries = valArray[0];
                String[] doiArr = valArray[1].split(":");
                doi = doiArr[0];
                String doiDataVal = req.getFullData(doi);
                HashMap<String,ArrayList<String>> metaData = HtmlParser.metaDataParser(doiDataVal);
                
                String acknowledgement = PlosUtil.getPlosOneAck(doiDataVal);
                String citation = PlosUtil.getPlosOneCitation(doiDataVal);
                String contributions = PlosUtil.getAuthorContributions(doiDataVal);
                plosData.setDoi(doi);
                plosData.setRelationUri(req.getRelationUriByDoi(doi));
                plosData.setUri(PlosUtil.DOI_PREFIX + doi);
                plosData.setAcknowledgements(acknowledgement);
                plosData.setAuthorContributions(contributions);
                plosData.setIsPartOfSeries(isPartOfSeries);
                plosData.setCitation(citation);
                
                Iterator it = metaData.entrySet().iterator();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); 
                
                try{
                    while(it.hasNext()){
                        Map.Entry pairs = (Map.Entry)it.next();
                        if(pairs.getKey().equals("citation_title")){
                            plosData.setTitle(pairs.getValue().toString().replaceAll("(\\[|\\])*", ""));
                        }
                        else if(pairs.getKey().equals("twitter:description")){
                            plosData.setAbstractText(pairs.getValue().toString().replaceAll("(\\[|\\])*", ""));
                        }
                        else if(pairs.getKey().equals("citation_date")){
                            String dateString = pairs.getValue().toString().replaceAll("(\\[|\\])*", "");
                            Date date = sdf.parse(dateString);
                            plosData.setDateIssued(date);
                        }
                        else if(pairs.getKey().equals("citation_author")){
                            plosData.setAuthors(pairs.getValue().toString().replaceAll("(\\[|\\])*", "").split(", "));
                        }
                        else if(pairs.getKey().equals("keywords")){
                            plosData.setSubjects(pairs.getValue().toString().replaceAll("(\\[|\\])*", "").split(", "));
                        }
                        //System.out.println(pairs.getKey() + " = " + pairs.getValue()+"\n\n");
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                    // download the PDF full text
                    req.downloadPlosOnePdfByDoi(doi);
                    PlosUtil.createContentFile(req.getImportedDataPath(doi)+"/content", doi.split("/")[1]+".pdf");
                    plosData.exportXmlByDoiData(req.getImportedDataPath(doi)+"/dublin_core.xml");
                    //System.exit(0);
                }
                catch(Exception ex){
                    System.out.print("The data processing from doiData to plosData is wrong!\n");
                    ex.printStackTrace();
                }
                plosDataList.add(plosData);
            }
        }
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
            Logger.getLogger(PlosDoiData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void exportXmlData(String filePath){
        
    }
    
}
