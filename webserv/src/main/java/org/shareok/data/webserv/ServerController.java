/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.redis.server.RepoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
            
            
            model = WebUtil.getRepoTypeList(model);
            model = WebUtil.getServerList(model, serverService);
            model.setViewName("serverConfig");
        }
        catch(Exception ex){
            model.addObject("errorMessage", "Cannot get the server list");
            model.setViewName("serverError");
            logger.error("Cannot retrieve and parse the server list.", ex);
        }        
        return model;
    }
    
    @RequestMapping("/server/update")
    public ModelAndView serverUpdate(RedirectAttributes redirectAttrs, HttpServletRequest request, @ModelAttribute("SpringWeb")RepoServer server){

        ModelAndView model = new ModelAndView();
        RedirectView view = new RedirectView();
        view.setContextRelative(true);
        RepoServer existingServer = null;
        
        /**
         * Some server side validation code:
         */
        boolean hasError = false;
        String serverId = (String)request.getParameter("serverId");
        if(null == server){
            redirectAttrs.addFlashAttribute("errorMessage", "The server information is empty");
            hasError = true;
        }
        String serverName = server.getServerName();
        if(null == serverName || "".equals(serverName) ){
            redirectAttrs.addFlashAttribute("errorMessage", "TThe server name is empty");
            hasError = true;            
        }
        if(null == serverId){
            redirectAttrs.addFlashAttribute("errorMessage", "The server ID is empty");
            hasError = true;
        }
        else if(serverId.equals("-1")){            
            existingServer = serverService.findServerByName(serverName);
            if(null != existingServer){
                redirectAttrs.addFlashAttribute("errorMessage", "The server name has been used");
                hasError = true;
            }
        }        
        
        if(hasError == true){
            view.setUrl("serverError.jsp");
            model.setView(view);
            return model;
        }
        
        if(null != serverId && serverId.equals("-1")){
            RepoServer newServer = serverService.addServer(server);
            Map<String, String> repoTypeServerFieldInfo = getRepoTypeServerFieldInfo(request, serverService.getRepoTypeServerFields(newServer.getRepoType()));
            serverService.updateRepoTypeServerFieldInfo(repoTypeServerFieldInfo, newServer);
            view.setUrl("/server/config");
            redirectAttrs.addFlashAttribute("message", "The new server \""+newServer.getServerName()+"\" has been added successfully!");
            model.setView(view);
        }
        else if(null != serverId && !serverId.equals("-1")){
            existingServer = serverService.updateServer(server);
            Map<String, String> repoTypeServerFieldInfo = getRepoTypeServerFieldInfo(request, serverService.getRepoTypeServerFields(existingServer.getRepoType()));
            serverService.updateRepoTypeServerFieldInfo(repoTypeServerFieldInfo, server);
            view.setUrl("/server/config");
            model.setView(view);
            redirectAttrs.addFlashAttribute("message", "The server \""+existingServer.getServerName()+"\" has been updated successfully!");
            return model; 
        }
        return model;
    }
    
    private Map<String, String> getRepoTypeServerFieldInfo(HttpServletRequest request, String[] fields){
        Map<String, String> fieldsInfo = new HashMap<>();
        if(null != fields && fields.length > 0){
            for(String field : fields){
                fieldsInfo.put(field, (String)request.getParameter(field));
            }
        }
        return fieldsInfo;
    }
}
