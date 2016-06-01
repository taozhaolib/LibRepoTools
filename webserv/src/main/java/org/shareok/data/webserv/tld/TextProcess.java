/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv.tld;

/**
 * Handles the conversion of job related texts
 * @author Tao Zhao
 */
public class TextProcess {
    public static String upTextFirstLetter(String text){
        if(null == text || "".equals(text)){
            return text;
        }
        String text2 = text.substring(1);
        String text1 = text.substring(0,1);
        return text1.toUpperCase() + text2;
    }
}
