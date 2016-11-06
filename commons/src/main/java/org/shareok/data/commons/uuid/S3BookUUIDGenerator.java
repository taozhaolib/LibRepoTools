/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.commons.uuid;

import java.util.UUID;

/**
 *
 * @author Tao Zhao
 */
public class S3BookUUIDGenerator extends ObjectUUIDGeneratorImpl {
    public UUID getPageUuidByName(String pageName){
        return super.getGenerator().generate(super.getObjectName()+"/data/"+pageName);
    }
}
