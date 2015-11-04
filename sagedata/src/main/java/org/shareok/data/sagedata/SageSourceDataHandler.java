/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

import java.util.HashMap;

/**
 *
 * @author Tao Zhao
 */
public interface SageSourceDataHandler {
    public void readSourceData();
    public HashMap getData();
    public void processSourceData();
    public void outputMetaData();
}
