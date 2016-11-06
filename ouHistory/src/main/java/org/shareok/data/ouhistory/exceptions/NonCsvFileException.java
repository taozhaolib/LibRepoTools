/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.ouhistory.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class NonCsvFileException extends Exception{
    /**
     *
     * @param message
     */
    public NonCsvFileException(String message){
        super(message);
    }
}
