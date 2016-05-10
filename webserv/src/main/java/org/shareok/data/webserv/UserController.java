/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.shareok.data.kernel.api.services.job.RedisJobService;
import org.shareok.data.kernel.api.services.user.RedisUserService;
import org.shareok.data.redis.job.RedisJob;
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
    public ModelAndView userJobHistory(@PathVariable("userId") String userId){
        try{
            long uid = Long.valueOf(userId);
            List<RedisJob> jobList = jobService.getJobListByUser(uid);
            return new ModelAndView("userJobHistory");
        }
        catch(Exception ex){
            logger.error("Cannot retrieve the job list of user "+userId, ex);
        }
        return null;
    }
}
