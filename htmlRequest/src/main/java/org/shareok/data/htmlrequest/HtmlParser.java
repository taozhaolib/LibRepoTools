/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.htmlrequest;

import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author Tao Zhao
 */
public class HtmlParser {
        public static HashMap<String,ArrayList<String>> metaDataParser(String html) {
            
            HashMap<String,ArrayList<String>> metaData = new HashMap<>();

            Document doc = Jsoup.parse(html.toString());
            //get meta description content
            Elements metaElements = doc.select("meta");
            
            int size = metaElements.size();
            if(size > 0){
                for(Element element : metaElements) {
                    if(element.hasAttr("name")) {
                        String name = element.attr("name");
                        if(!metaData.containsKey(name)){
                            ArrayList<String> list = new ArrayList<>();
                            list.add(element.attr("content"));
                            metaData.put(name, list);
                            
                        }
                        else{
                            ArrayList<String> list = metaData.get(name);
                            String content = element.attr("content");
                            list.add(content);
                            metaData.put(name, list);
                        }
                    }
                }
            }

            return metaData;
        }
        
}
