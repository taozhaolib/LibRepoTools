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
public interface ObjectUUIDGenerator {
    public UUID getObjectUuidV5();
    public void setObjectName(String objectName);
}
