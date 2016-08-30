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
public class SafPackageMissingFileException extends Exception {
    /**
     *
     * @param message
     */
    public SafPackageMissingFileException(String message){
        super(message);
    }
}

