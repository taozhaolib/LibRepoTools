/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services;

import org.shareok.data.config.DataHandler;

/**
 *
 * @author Tao Zhao
 */
public interface DataService {
    public void setHandler(DataHandler handler);
    public String executeTask(String jobType);
}
