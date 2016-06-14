/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        api.setDspaceUserName("tao.zhao.lib@gmail.com");
        api.setDspacePassword("tao");
        api.setToken("78e87b26-2a0c-4b22-ac10-b1cdbea9ec00");
        //String[] ids = api.getItemIdsByCollectionHandler("11244/33301");
        //List map = api.getItemMetadataById("43988");
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> entry1 = new HashMap<>();
        Map<String, String> entry2 = new HashMap<>();
        entry1.put("key", "dwc.npdg.isolatesRBM");
        entry1.put("value", "2");
        entry1.put("language", "en");
        entry2.put("value", "3");
        entry2.put("language", "chn");
        entry2.put("key", "dwc.npdg.isolatesTV8");
        data.add(entry2);
        data.add(entry1);
        //api.updateItemMetadata("40184", data);
        Map userInfo = api.getUserInfoByToken();
        boolean auth = api.isAuthorizedUser();
        System.out.println(entry1.toString());
    }
}
