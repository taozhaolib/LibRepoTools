/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("dspaceManagerContext.xml");
        DspaceSshHandler dspaceSshHandler = (DspaceSshHandler)context.getBean("dspaceSshHandler");
        dspaceSshHandler.setUserName("ok");
    }
}
