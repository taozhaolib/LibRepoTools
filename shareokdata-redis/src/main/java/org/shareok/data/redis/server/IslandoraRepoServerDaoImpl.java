/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

import java.lang.reflect.Field;
import java.util.Arrays;
import org.shareok.data.config.DataUtil;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.exceptions.IncompleteServerInformationException;
import org.springframework.data.redis.core.BoundHashOperations;

/**
 *
 * @author Tao Zhao
 */
public class IslandoraRepoServerDaoImpl implements RepoTypeServerDao {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IslandoraRepoServerDaoImpl.class);

    @Override
    public RepoServer loadServerParametersByRepoType(RepoServer server, BoundHashOperations<String, String, String> serverOps) {
        IslandoraRepoServer iServer = null;
        try{
            int repoType = server.getRepoType();
            if(repoType != Arrays.asList(DataUtil.REPO_TYPES).indexOf("islandora")){
                throw new IncompleteServerInformationException("Server repository type information is missing!");
            }
            iServer = (IslandoraRepoServer) getRepoTypeServerFromAbstract(server);
            iServer.setDrupalPath((String)serverOps.get("drupalPath"));
            iServer.setIslandoraUploadPath((String)serverOps.get("islandoraUploadPath"));
            iServer.setTempFilePath((String)serverOps.get("tempFilePath"));
        }
        catch(IncompleteServerInformationException ex){
            logger.error(ex.getMessage());
        }
        return iServer;
    }

    @Override
    public RepoServer getRepoTypeServerFromAbstract(RepoServer server) {
        IslandoraRepoServer iServer = RedisUtil.getIslandoraRepoServerInstance();
        Field[] fields = server.getClass().getDeclaredFields();
        for(Field field : fields){
            String key = field.getName();
            field.setAccessible(true);
            Object val;
            try {
                val = field.get(server);
                if(null != val){
                    Field iField = null;
                    try {
                        iField = iServer.getClass().getSuperclass().getDeclaredField(key);                                             
                    } catch (NoSuchFieldException ex) {                        
                        logger.debug("This field "+key+" does not exist!");
                        continue;
                    } catch (SecurityException ex) {
                        logger.debug("Security exception when handles field "+key+"!");
                        continue;
                    }
                    iField.setAccessible(true);
                    iField.set(iServer, val);   
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                logger.error("Cannot get property values from server: " + ex.getMessage());
            }
            
        }
        return iServer;
    }
    
}
