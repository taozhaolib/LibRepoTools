/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class NullUserException extends Exception {  
    /**
     *
     * @param message
     */
    public NullUserException(String message){
        super(message);
    }

}
