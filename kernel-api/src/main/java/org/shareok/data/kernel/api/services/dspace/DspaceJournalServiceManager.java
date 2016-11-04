/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.shareok.data.config.ShareokdataManager;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tao Zhao
 */

@Service
@Configurable
public class DspaceJournalServiceManager {
    public static DspaceJournalDataService getDspaceJournalDataService(String publisher){
        DspaceJournalDataService djds = null;
        try{
            ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
            djds = (DspaceJournalDataService)context.getBean(ShareokdataManager.getJournalDataServiceBean(publisher));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return djds;
    }
    
    public static DspaceSafPackageDataService getDspaceSafPackageDataService(String publisher){
        DspaceSafPackageDataService dspds = null;
        try{
            ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
            dspds = (DspaceSafPackageDataService)context.getBean(ShareokdataManager.getSafPackageDataServiceBean(publisher));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return dspds;
    }
}
