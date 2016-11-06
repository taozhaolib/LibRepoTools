/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Tao Zhao
 */
public class MissingCvsColumnException extends Exception{
    
    /**
     *
     * @param message
     */
    public MissingCvsColumnException(String message){
        super(message);
    }
    
}
