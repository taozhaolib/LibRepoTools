/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.sagedata;

/**
 *
 * @author Tao Zhao
 */
public interface SageApiDataHandler {
    public String getApiResponseByDatesAffiliate(String startDate, String endDate, String affiliate);
    public String outputResponse();
}
