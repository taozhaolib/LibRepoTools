/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.documentProcessor.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class FileTypeException extends Exception{
    
    /**
     *
     * @param message
     */
    public FileTypeException(String message){
        super(message);
    }
    
    /**
     * 
     * @param expectedType : String
     * @param actualType : String
     */
    public FileTypeException(String expectedType, String actualType){
        super("The expected file type is "+expectedType+" but the actual file type is "+actualType);
        
    }

}
