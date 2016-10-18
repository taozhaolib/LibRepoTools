/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.htmlrequest.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class ErrorOpenConnectionException extends Exception {
    /**
     *
     * @param message
     */
    public ErrorOpenConnectionException(String message){
        super(message);
    }
}