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
public class NoServiceProcessDoiException extends Exception {
    
    /**
     *
     * @param message
     */
    public NoServiceProcessDoiException(String message){
        super(message);
    }
}