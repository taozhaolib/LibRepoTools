/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.ssh;

import com.jcraft.jsch.JSchException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("sshContext.xml");
        SshConnector connector = (SshConnector)context.getBean("sshConnector");
        connector.setHost("172.28.128.7");
        connector.setPort(22);
        connector.setTimeout(5000);
        connector.setUserName("vagrant");
        connector.setPassword("vagrant");
        SshExecutor exec = (SshExecutor) context.getBean("sshExecutor");
        exec.setSshConnector(connector);
        try{
            //exec.getConnect();
            //exec.upload("/home/vagrant", "/Users/zhao0677/Projects/shareokdata/test-load.zip");
            //exec.execCmd("sudo /srv/dspace/bin/dspace version");
            //exec.execCmd("sudo unzip /home/vagrant/test-load.zip");
            exec.execCmd("sudo /srv/dspace/bin/dspace import --add --eperson=tao.zhao@ou.edu --collection=123456789/2 --source=/home/vagrant/test-load --mapfile=/home/vagrant/mapfile");
        }
        catch(Exception jex){
            jex.printStackTrace();
        }
    }
}
  