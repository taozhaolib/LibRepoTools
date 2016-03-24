/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Tao Zhao
 */
@Controller
@Configuration
@PropertySource("classpath:shareokdata-web.properties")
public class LoginPageController{
   
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
         ModelAndView model = new ModelAndView();
         String email = (String)req.getParameter("email");
         model.addObject("email", email);
         HttpSession session = req.getSession();
         if(null != session){
            String userName = (String)session.getAttribute("userName");
            model.addObject("userName", userName);
            String userId = (String)session.getAttribute("userId");
            model.addObject("userId", userId);
            String isActive = (String)session.getAttribute("isActive");
            model.addObject("isActive", isActive);
         }
         model.setViewName("home");

         return model;
    }
    
    @RequestMapping("/userLogin")
    public String userLogin(ModelMap model) {
         

         return "home";
    }
    
    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest req, HttpServletResponse res) {
         ModelAndView model = new ModelAndView();
//         String email = (String)req.getParameter("email");
//         model.addObject("email", email);
//         HttpSession session = req.getSession();
//         if(null != session){
//            String userName = (String)session.getAttribute("userName");
//            model.addObject("userName", userName);
//            String userId = (String)session.getAttribute("userId");
//            model.addObject("userId", userId);
//            String isActive = (String)session.getAttribute("isActive");
//            model.addObject("isActive", isActive);
//         }
         model.setViewName("home");

         return model;
    }
}
