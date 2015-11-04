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
public class DataTypeException extends Exception{
    
    /**
     *
     * @param message
     */
    public DataTypeException(String message){
        super(message);
    }
    
    /**
     * 
     * @param expectedType : String
     * @param actualType : String
     */
    public DataTypeException(String expectedType, String actualType){
        super("The expected date type is "+expectedType+" but the actual date type is "+actualType);
        
    }

}
