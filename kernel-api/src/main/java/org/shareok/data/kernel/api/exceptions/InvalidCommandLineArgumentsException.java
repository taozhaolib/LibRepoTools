/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class InvalidCommandLineArgumentsException extends Exception {
    
    /**
     *
     * @param message
     */
    public InvalidCommandLineArgumentsException(String message){
        super(message);
    }
}