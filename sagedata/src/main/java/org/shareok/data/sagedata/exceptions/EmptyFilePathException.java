/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class EmptyFilePathException extends Exception{
    
    /**
     *
     * @param message
     */
    public EmptyFilePathException(String message){
        super(message);
    }
    
    /**
     * 
     * @param expectedType : String
     * @param actualType : String
     */
    public EmptyFilePathException(){
        super("The File Path is NOT set!");
        
    }

}
