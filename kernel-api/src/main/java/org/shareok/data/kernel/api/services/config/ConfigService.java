/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.config;

/**
 *
 * @author Tao Zhao
 */
public interface ConfigService {
    public void updateRegistrationConfig(boolean allowRegistration);
    public boolean getRegistrationConfig();    
}
