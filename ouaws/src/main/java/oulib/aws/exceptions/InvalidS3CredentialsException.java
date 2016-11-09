/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oulib.aws.exceptions;

/**
 *
 * @author Tao Zhao
 */
public class InvalidS3CredentialsException extends Exception {
    /**
     *
     * @param message
     */
    public InvalidS3CredentialsException(String message){
        super(message);
    }
}
