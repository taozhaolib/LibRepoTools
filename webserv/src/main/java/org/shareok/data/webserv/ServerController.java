/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.shareok.data.config.DataUtil;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.redis.server.RepoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Tao Zhao
 */
@Controller
@Configuration
@PropertySource("classpath:shareokdata-web.properties")
public class ServerController {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserController.class);
    
    private RepoServerService serverService;
    
    public RepoServerService getServerService() {
        return serverService;
    }

    @Autowired
    public void setServerService(RepoServerService serverService) {
        this.serverService = serverService;
    }
    
    @RequestMapping("/server/config")
    public ModelAndView serverConfig(HttpServletRequest request){

        HttpSession session = request.getSession();
//        String userRole = (String) session.getAttribute("userRole");        
        ModelAndView model = new ModelAndView();
        
        try{
//            if(null == userRole || !userRole.equals("admin")){
//                model.setViewName("userError");
//                return model;
//            }

            Map<String, String> serverList = serverService.getServerNameIdList();

            if(null != serverList && serverList.size() > 0){
                
                ObjectMapper mapper = new ObjectMapper();
                
                Collection<String> ids = serverList.values();
                List<RepoServer> serverObjList = serverService.getServerObjList(ids);
//                List<RepoServer> repoServerObjList = serverService.loadRepoServerListByRepoType(serverObjList);
                
//                for(RepoServer server : repoServerObjList){
//                    String repoType = DataUtil.REPO_TYPES[server.getRepoType()];
//                    if("dspace".equals(repoType)){
//                        DspaceServer ds = (DspaceServer)server;
//                        model.addObject(String.valueOf(ds.getServerId()), mapper.writeValueAsString(ds));
//                    }
//                }
                
                String serverListJson = mapper.writeValueAsString(serverList);
                model.addObject("serverList", serverListJson);
                //model.addObject("serverObjList", mapper.writeValueAsString(serverObjList));
            }
            else{
                model.addObject("emptyServerList", "There are NO servers set up.");
            }

            model.setViewName("serverConfig");
        }
        catch(Exception ex){
            logger.error("Cannot retrieve and parse the server list.", ex);
        }
        
        return model;
    }
}
