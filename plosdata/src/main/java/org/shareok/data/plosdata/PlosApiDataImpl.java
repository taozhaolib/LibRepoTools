/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.plosdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.shareok.data.plosdata.exception.InvalidPlosApiQueryException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.shareok.data.documentProcessor.FileUtil;
import org.shareok.data.htmlrequest.HttpRequestHandler;
import org.shareok.data.plosdata.exception.ErrorPlosApiResponseException;
import org.shareok.data.plosdata.exception.NoDoiDataException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Tao Zhao
 */
public class PlosApiDataImpl implements PlosApiData {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PlosApiDataImpl.class);
    
    private String startDate;
    private String endDate;
    private String affiliate;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getAffiliate() {
        return affiliate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setAffiliate(String affiliate) {
        this.affiliate = affiliate;
    }
    
    @Override
    public String getApiResponse() {
        String jsonData = null;
        String query = getApiQuery();        
        try{
            if(null != query){System.out.println(query);
//                query = "http://api.plos.org/search?q=author_affiliate%3A%22University%20of%20Oklahoma%22%20AND%20publication_date:[2016-01-01T00:00:00Z+TO+2016-12-31T23:59:59Z]&rows=500&fl=affiliate,doi,author,volume,issue,title,journal,publication_date&api_key=mC8TEAzqwfvDWs4eamFh";
                ApplicationContext context = new ClassPathXmlApplicationContext("htmlRequestContext.xml");
                HttpRequestHandler req = (HttpRequestHandler)context.getBean("httpRequestHandler");
                String response = req.sendGet(query);
                String[] responseInfoArr = response.split("\\n");
                if(null != responseInfoArr[0] && responseInfoArr[0].equals("200")){
                    Map<String, Map<String, String>> mapData = new HashMap<>();
                    String responseDocs = responseInfoArr[1];
                    if(!FileUtil.isEmptyString(responseDocs)){
                        Document doc = FileUtil.loadXMLFromStringContent(responseDocs);
                        NodeList docList = doc.getElementsByTagName("doc");
                        for(int i = 0; i < docList.getLength(); i++){
                            Element item = (Element) docList.item(i);
                            Map<String, String> tmpMapData = getArticleMapData(item);
                            if(tmpMapData.containsKey("doi")){
                                mapData.put(tmpMapData.get("doi"), tmpMapData);
                            }
                            else{
                                throw new NoDoiDataException("Parseing the api response did not get DOI information!");
                            }
                        }
                    }
                    ObjectMapper mapper = new ObjectMapper();             
                    jsonData = mapper.writeValueAsString(mapData);
                }
                else{
                    throw new ErrorPlosApiResponseException("Got the response code "+responseInfoArr[0]);
                }
            }
            else{
                throw new InvalidPlosApiQueryException("The plos api query is null");
            }
        }
        catch(Exception ex){
            logger.error(ex);
        }
        return jsonData;
    }

    @Override
    public String outputResponse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private String getApiQuery(){
        try {
            return PlosUtil.API_SEARCH_PREFIX + URLEncoder.encode("author_affiliate:\"" + affiliate + "\" AND publication_date:[", "UTF-8") +
                    startDate + "T00:00:00Z" + URLEncoder.encode(" TO ", "UTF-8") +
                    endDate + "T23:59:59Z]&rows=" + PlosUtil.API_SEARCH_ROW + "&fl=" + PlosUtil.API_SEARCH_FACETS + 
                    "&api_key=" + PlosUtil.API_KEY;
        } catch (UnsupportedEncodingException ex) {
            logger.error("Cannot encode the query parameters!", ex);
        }
        return null;
    }
    
    private Map<String, String> getArticleMapData(Element ele){
        Map<String, String> mapData = new HashMap<>();
//        String[] tagNames = new String[]{"arr", "str", "int", "date"};
        NodeList eleChildren = ele.getChildNodes();
        for(int childIndex = 0; childIndex < eleChildren.getLength(); childIndex++){
            Node node = eleChildren.item(childIndex);
            String nodeName = node.getNodeName();
            NamedNodeMap attributes = node.getAttributes();
            if(attributes.getLength() > 0){
                Node item = attributes.item(0);
                String attributeVal = item.getNodeValue();
                String val = "";
                if(nodeName.equals("arr")){
                    NodeList children = node.getChildNodes();
                    for(int j = 0; j < children.getLength(); j++){
                        Node child = children.item(j);
                        if(child.getNodeName().equals("str")){
                            val += child.getTextContent()+"||";
                        }
                    }
                }
                else{
                    val = node.getTextContent();
                }
                mapData.put(attributeVal, val);
            }
        }
        
        return mapData;
    }
}
