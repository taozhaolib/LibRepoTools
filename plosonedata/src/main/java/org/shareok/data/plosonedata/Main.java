/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosonedata;

import java.util.HashMap;
import org.shareok.data.msofficedata.ExcelHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:**/officeContext.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("plosOneContext.xml");
        PlosOneDoiData obj = (PlosOneDoiData) context.getBean("plosOneDoiData");
        obj.importData("plos_articles.xlsx");
        obj.getMetaData();
        //ExcelHandler obj = (ExcelHandler) context.getBean("excelHandler");
        //obj.setFileName("ok");
    }
}
