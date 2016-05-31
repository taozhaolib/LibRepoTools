/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.job.RedisJobService;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.kernel.api.services.user.RedisUserService;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.RedisJob;
import org.shareok.data.redis.server.DspaceServer;
import org.shareok.data.redis.server.RepoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Tao Zhao
 */
@Controller
@Configuration
@PropertySource("classpath:shareokdata-web.properties")
public class UserController{
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UserController.class);
    
    private RedisUserService userService;
    private RedisJobService jobService;

    public RedisUserService getUserService() {
        return userService;
    }

    public RedisJobService getJobService() {
        return jobService;
    }

    @Autowired
    public void setJobService(RedisJobService jobService) {
        this.jobService = jobService;
    }

    @Autowired
    public void setUserService(RedisUserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping("/login")
    public ModelAndView loginPage() {
         ModelAndView model = new ModelAndView();
         model.setViewName("login");

         return model;
    }
    
    @RequestMapping("/newUser")
    public ModelAndView newUserPage() {
         ModelAndView model = new ModelAndView();
         model.setViewName("newUser");

         return model;
    }
    
    @RequestMapping("/register")
    public ModelAndView register(HttpServletRequest req, HttpServletResponse res) {
         return new ModelAndView("redirect:home");
    }
    
    @RequestMapping("/userLogin")
    public ModelAndView userLogin(HttpServletRequest req) {
        
        return new ModelAndView("redirect:home");
    }
    
    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest req, HttpServletResponse res) {
        return new ModelAndView("redirect:home");
    }
    
    @RequestMapping("/user/{userId}/jobHistory")
    public ModelAndView userJobHistory(HttpServletRequest request, @PathVariable("userId") String userId){
        
        ModelAndView model = new ModelAndView();
        model.setViewName("userJobHistory");
        String email = (String)request.getSession().getAttribute("email");
        String nickName = (String) request.getSession().getAttribute("nickname");
        model.addObject("email", email);
        model.addObject("nickName", nickName);
        
        try{
            long uid = Long.valueOf(userId);
            List<RedisJob> jobList = jobService.getJobListByUser(uid);
            List<Map<String,String>> parsedJobList = new ArrayList<Map<String, String>>();
            Map<String, String> parsedJob;
            int size = jobList.size();
            if(size > 0){                
                ObjectMapper mapper = new ObjectMapper();
                for(int i = 0; i < jobList.size(); i++){
                    RedisJob job = jobList.get(i);
                    if(null != job){
                        parsedJob = new HashMap<String, String>();
                        parsedJob.put("jobId", String.valueOf(job.getJobId()));
                        parsedJob.put("jobType", DataUtil.JOB_TYPES[job.getType()]);
                        parsedJob.put("repoType", DataUtil.REPO_TYPES[job.getRepoType()]);
                        parsedJob.put("status", RedisUtil.REDIS_JOB_STATUS[job.getStatus()]);
                        parsedJob.put("userId", String.valueOf(job.getUserId()));
                        parsedJob.put("startTime", ShareokdataManager.getSimpleDateFormat().format(job.getStartTime()));
                        parsedJob.put("endTime", ShareokdataManager.getSimpleDateFormat().format(job.getEndTime()));
                        parsedJobList.add(parsedJob);
                    }
                }
                String jobListJson = mapper.writeValueAsString(parsedJobList);
                model.addObject("jobList", jobListJson);
            }
            return model;
        }
        catch(NumberFormatException ex){
            logger.error("Cannot retrieve the job list of user "+userId, ex);
        } catch (JsonProcessingException ex) {
            logger.error("Cannot process the job list of user "+ userId + " into JSON data.", ex);
        }
        return null;
    }
    
    @RequestMapping("/server/config")
    public ModelAndView serverConfig(HttpServletRequest request){

        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");        
        ModelAndView model = new ModelAndView();
        
        try{
            if(null == userRole || !userRole.equals("admin")){
                model.setViewName("userError");
                return model;
            }

            RepoServerService service = ServiceUtil.getRepoServerServiceInstance(null);
            Map<String, String> serverList = service.getServerNameIdList();

            if(null != serverList && serverList.size() > 0){
                
                ObjectMapper mapper = new ObjectMapper();
                
                Collection<String> ids = serverList.values();
                List<RepoServer> serverObjList = service.getServerObjList(ids);
                List<RepoServer> repoServerObjList = service.loadRepoServerListByRepoType(serverObjList);
                
                for(RepoServer server : repoServerObjList){
                    String repoType = DataUtil.REPO_TYPES[server.getRepoType()];
                    if("dspace".equals(repoType)){
                        DspaceServer ds = (DspaceServer)server;
                        model.addObject(String.valueOf(ds.getServerId()), mapper.writeValueAsString(ds));
                    }
                }
                
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
