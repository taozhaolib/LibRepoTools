/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.islandoramanager;

import org.shareok.data.ssh.SshExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class IslandoraSshDataUtil {

    public static SshExecutor getSshExecForIslandora() {
        ApplicationContext context = new ClassPathXmlApplicationContext("sshContext.xml");
        SshExecutor sshExecutor = (SshExecutor) context.getBean("sshExecutor");
        return sshExecutor;
    }
}