/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.services.ServiceUtil;
import org.shareok.data.kernel.api.services.job.RedisJobService;
import org.shareok.data.kernel.api.services.server.RepoServerService;
import org.shareok.data.kernel.api.services.user.PasswordAuthenticationService;
import org.shareok.data.kernel.api.services.user.RedisUserService;
import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.RedisUtil;
import org.shareok.data.redis.job.RedisJob;
import org.shareok.data.redis.server.RepoServer;
import org.shareok.data.webserv.exceptions.IncorrectUserCredentialInfoException;
import org.shareok.data.webserv.exceptions.UserRegisterInfoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
    private RepoServerService serverService;
    private PasswordAuthenticationService pwAuthenService;

    public RedisUserService getUserService() {
        return userService;
    }

    public RedisJobService getJobService() {
        return jobService;
    }

    @Autowired
    @Qualifier("redisJobServiceImpl")
    public void setJobService(RedisJobService jobService) {
        this.jobService = jobService;
    }
    
    @Autowired
    public void setServerService(RepoServerService serverService){
        this.serverService = serverService;
    }

    @Autowired
    public void setUserService(RedisUserService userService) {
        this.userService = userService;
    }
    
    @Autowired
    public void setPwAuthenService(PasswordAuthenticationService pwAuthenService){
        this.pwAuthenService = pwAuthenService;
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
                        Date startTime = job.getStartTime();
                        Date endTime = job.getEndTime();
                        parsedJob.put("jobId", String.valueOf(job.getJobId()));
                        int jobType = job.getType();
                        if(jobType < 0 || jobType >= DataUtil.JOB_TYPES.length){
                            logger.debug("The job type " + String.valueOf(jobType) + " does not exist!");
                            continue;
                        }
                        parsedJob.put("jobType", DataUtil.JOB_TYPES[jobType]);
                        int status = job.getStatus();
                        if(status < 0 || status >= RedisUtil.REDIS_JOB_STATUS.length){
                            logger.debug("The job status " + String.valueOf(status) + " does not exist!");
                            continue;
                        }
                        parsedJob.put("status", RedisUtil.REDIS_JOB_STATUS[status]);
                        parsedJob.put("userId", String.valueOf(job.getUserId()));
                        parsedJob.put("startTime", (null == startTime) ? "" : ShareokdataManager.getSimpleDateFormat().format(startTime));
                        parsedJob.put("endTime", (null == endTime) ? "" : ShareokdataManager.getSimpleDateFormat().format(endTime));
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
    
    @RequestMapping("/report/job/{jobId}")
    public ModelAndView userJobSummary(HttpServletRequest request, @PathVariable("jobId") String jobId){
        
        ModelAndView model = new ModelAndView();
        RedisJob job = jobService.findJobByJobId(Long.parseLong(jobId));
        RepoServer server = serverService.findServerById(job.getServerId());
        RedisJobService specificService = ServiceUtil.getJobServiceByJobType(job);
        Map<String, String> data = specificService.getReportData(specificService.findJobByJobId(Long.parseLong(jobId)));
                
        if(null != job){
            int statusIndex = job.getStatus();
            String isFinished = (statusIndex == 2 || statusIndex == 6) ? "true" : "false";

            model.setViewName("jobReport");
            if(null != data){
                if(null != data.get("Collection")){
                    model.addObject("collection", (String)data.get("Collection"));
                }
            }
            
            model.addObject("repoType", ServiceUtil.getRepoTypeByJob(job));
            model.addObject("isFinished", isFinished);
            model.addObject("reportPath", ServiceUtil.getReportFilePathByJob(job));  
            model.addObject("host", server.getHost());
            WebUtil.outputJobInfoToModel(model, job);
        }
        else{
            model.setViewName("jobError");
            model.addObject("errorMessage", "Cannot retrieve the job information.");
        }

        return model;
    }
    
    @RequestMapping("/userProfile")
    public ModelAndView userProfilePage(HttpServletRequest request){
        
        ModelAndView model = new ModelAndView();
        model.setViewName("userProfile");

        return model;
    }
    
    @RequestMapping("/user/newPass")
    public ModelAndView userNewPass(HttpServletRequest request){
        
        ModelAndView model = new ModelAndView();
        
        String oldPass = (String)request.getParameter("oldPass");
        String newPass = (String)request.getParameter("newPass");
        String newPassConfirm = (String)request.getParameter("newPassConfirm");
        if(null != oldPass && !oldPass.equals("") && null != newPass && !newPass.equals("") && null != newPassConfirm && !newPassConfirm.equals("")){
            if(!newPass.equals(newPassConfirm)){
                try {
                    throw new IncorrectUserCredentialInfoException("The two new passwords do not match!");
                } catch (IncorrectUserCredentialInfoException ex) {
                    logger.error(ex);
                    model.addObject("errorMessage", "The two new passwords do not match!");
                    model.setViewName("userError");
                    return model;
                }
            }
            String email = (String)request.getSession().getAttribute("email");
            RedisUser user = userService.findUserByUserEmail(email);
            if(null == user || !pwAuthenService.authenticate(oldPass, user.getPassword())){
                String message = null;
                try {                    
                    if(null == user){
                        message = "User information cannot by found by user email!";
                    }
                    else{
                        message = "User old password is incorrect!";
                    }
                    throw new UserRegisterInfoNotFoundException(message);
                } catch (UserRegisterInfoNotFoundException ex) {
                    logger.error(ex);
                    model.addObject("errorMessage", message);
                    model.setViewName("userError");
                    return model;
                }
            }
            // Check if the user is the current user in session
            String sessionId = (String)request.getSession().getId();
            String userSessionId = user.getSessionKey();
            if(null == userSessionId || !userSessionId.equals(sessionId)){
                try {
                    throw new UserRegisterInfoNotFoundException("The user session ID does not match the current session ID!");
                } catch (UserRegisterInfoNotFoundException ex) {
                    logger.error(ex);
                    model.addObject("errorMessage", "The user session ID does not match the current session ID!");
                    model.setViewName("userError");
                    return model;
                }
            }            
            String password = pwAuthenService.hash(newPass);
            user.setPassword(password);
            userService.updateUser(user);
            RedirectView view = new RedirectView();
            view.setContextRelative(true);
            view.setUrl("/userProfile");
            model.setView(view);
            return model;
        }
        else{
            try {
                throw new IncorrectUserCredentialInfoException("Some password information is empty for resetting user password!");
            } catch (IncorrectUserCredentialInfoException ex) {
                logger.error(ex);
                model.addObject("errorMessage", "Some password information is empty for resetting user password!");
                model.setViewName("userError");
            }
        }        
        return model;
    }
}
