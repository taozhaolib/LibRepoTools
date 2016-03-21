/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.redis.RedisUser;
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
public class HomeController{

    @RequestMapping("/home")
    public ModelAndView home(HttpServletRequest req, HttpServletResponse res) {

        ModelAndView model = new ModelAndView();
        model.setViewName("home");
        HttpSession session = (HttpSession) req.getSession(false);
        if(null != session){
            RedisUser user = (RedisUser) session.getAttribute(ShareokdataManager.getSessionRedisUserAttributeName());
            if(null != user){
                Cookie userCookie = new Cookie("userId", String.valueOf(user.getUserId()));
                userCookie.setMaxAge(30*60);
                res.addCookie(userCookie);
                model.addObject("user", user);
                model.addObject("loginTime", session.getCreationTime());
            }        
        }
        return model;
    }
}
