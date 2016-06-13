/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.dspacemanager.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class EmptyDspaceCredentialInfoException extends Exception {
    /**
     *
     * @param message
     */
    public EmptyDspaceCredentialInfoException(String message){
        super(message);
    }
}
