/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.shareok.data.plosdata;

import java.util.HashMap;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public interface ExcelData {
    public void addData(int row, int col, Object data);
    public void printData();
    public void importData (String fileName);
    public void exportXmlData(String filePath);
    public void convertHashMapDataToDoi(HashMap mapData) throws Exception;
}
