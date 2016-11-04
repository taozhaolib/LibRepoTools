/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.ouhistory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class OuHistoryJournalDataUtil {
    
    public static OuHistoryJournalData getOuHistoryJournalDataInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("ouHistoryDataContext.xml");
        return (OuHistoryJournalData) context.getBean("ouHistoryJournalData");
    }
    
    public static OuHistoryJournalDataProcessor getOuHistoryJournalDataProcessorInstance(){
        ApplicationContext context = new ClassPathXmlApplicationContext("ouHistoryDataContext.xml");
        return (OuHistoryJournalDataProcessor) context.getBean("ouHistoryJournalDataProcessorImpl");
    }
}
