/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.commons.uuid;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Tao Zhao
 */
public class ObjectUUIDGeneratorImpl implements ObjectUUIDGenerator{
    
    private static final String UUID_PREFIX = "repository.ou.edu";
            
    private String objectName;
    private NameBasedGenerator generator;

    public ObjectUUIDGeneratorImpl() {
        try {
            generator = Generators.nameBasedGenerator(NameBasedGenerator.NAMESPACE_DNS, MessageDigest.getInstance("SHA1"));
            generator = Generators.nameBasedGenerator(getRepoUuidV5(), MessageDigest.getInstance("SHA1"));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ObjectUUIDGeneratorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getObjectName() {
        return objectName;
    }

    public NameBasedGenerator getGenerator() {
        return generator;
    }

    @Override
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public void setGenerator(NameBasedGenerator generator) {
        this.generator = generator;
    }
    
    @Override
    public UUID getObjectUuidV5(){        
        return generator.generate(objectName);
    }
    
    private UUID getRepoUuidV5() throws NoSuchAlgorithmException{
        return generator.generate(UUID_PREFIX);
    }
}
