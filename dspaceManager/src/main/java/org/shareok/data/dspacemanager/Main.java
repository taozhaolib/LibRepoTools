/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import com.fasterxml.jackson.databind.MappingIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("dspaceManagerContext.xml");
//        DspaceSshHandler dspaceSshHandler = (DspaceSshHandler)context.getBean("dspaceSshHandler");
//        dspaceSshHandler.setUserName("ok");
        DspaceApiHandlerImpl api = (DspaceApiHandlerImpl)context.getBean("dspaceApiHandlerImpl");
        api.setDspaceApiUrl("https://test.shareok.org/rest");
//        api.setDspaceUserName("tao.zhao.lib@gmail.com");
//        api.setDspacePassword("tao");
//        String token = api.getTokenFromServer();
//        System.out.println("The token is "+token);
        api.setToken("ccdd2de1-dc5a-481c-9337-67d2904cb9ad");
//        api.deleteItemsByCollectionId(api.getObjectIdByHandler("11244/33301"));
//        api.loadItemsFromSafPackage("/Users/zhao0677/Projects/law-library/SimpleArchiveFormat", "11244/35123");
//        Map response = api.addItemBitstream("45705", 
//                "/Users/zhao0677/Projects/law-library/SimpleArchiveFormat/item_1/Senate-26-1-Document-598-Serial-361.pdf", 
//                "senate.pdf", "a_test_bitstream.pdf");
//        System.out.println(" id = "+response.get("id"));
//        api.deleteItemById(api.getObjectIdByHandler("11244/34945"));
//        String id = api.getObjectIdByHandler("11244/34980");
//        String[] metadata = api.getMetadataFromXmlFiles(new String[]{"/Users/zhao0677/Projects/law-library/saf2/item_42/dublin_core.xml"});
//        for(String data : metadata){
//            System.out.println("Adding metadata -- "+data+":");
//            api.addItemMetadata(id, data);
//        }
//        Map response = api.createEmptyItem("395");
//        if(response.containsKey("id")){
//            System.out.println("The id is "+(String)response.get("id"));
//        }
//        else{
//            System.out.println("Did not get the new item");
//        }
//        System.out.println(" id = "+api.getObjectIdByHandler("11244/34507"));
//        List metadata = api.getItemMetadataById("45705");
//        ListIterator it = metadata.listIterator();
//        while(it.hasNext()){
//            Map itemMap = (HashMap)it.next();
//            System.out.println("Entry is "+itemMap.get("key")+"; value ="+itemMap.get("value")+"; language ="+itemMap.get("language"));
//            
//        }
//        String[] ids = api.getItemIdsByCollectionHandler("11244/33301");
//        for(String id : ids){
////        List map = api.getItemMetadataById(id);
//                System.out.println("id = "+id);
//        }
//        List<Map<String, String>> data = new ArrayList<>();
//        Map<String, String> entry1 = new HashMap<>();
//        Map<String, String> entry2 = new HashMap<>();
//        entry1.put("key", "dc.contributor.author");
//        entry1.put("value", "Tao Zhao");
//        entry1.put("language", "en");
//        entry2.put("value", "Make it simple okokok");
//        entry2.put("language", "chn");
//        entry2.put("key", "dc.description.abstract");
//        data.add(entry2);
//        data.add(entry1);
//        api.updateItemMetadata("44972", data);
//        Map userInfo = api.getUserInfoByToken();
//        boolean auth = api.isAuthorizedUser();
    }
}
