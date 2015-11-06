/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class SageJournalDataProcessorFactory {
    
    public static SageJournalDataProcessor getSageJournalDataProcessorByName(String journalName){
        Map journalMap = SageDataUtil.getJournalListWithBeans();
        return getSageJournalDataProcessorByName(journalMap, journalName);
    }
    
    public static SageJournalDataProcessor getSageJournalDataProcessorByName(Map journalMap, String journalName){
        
        SageJournalDataProcessor sjdp = null;
        
        if(null == journalMap){
            journalMap = SageDataUtil.getJournalListWithBeans();
        }
                
        if(journalMap.containsKey(journalName))
        {
            String bean = (String)journalMap.get(journalName);
            ApplicationContext context = new ClassPathXmlApplicationContext("sageDataContext.xml");
            sjdp = (SageJournalDataProcessor) context.getBean(bean);
        }
        
        return sjdp;
    }
}
