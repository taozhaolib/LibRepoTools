/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.documentProcessor.exceptions;

/**
 * Exception when file name is empty, undefined, or wrong name type
 * 
 * @author Tao Zhao
 */
public class FileNameException extends Exception{
    public FileNameException(String message){
        super(message);
    }
}
