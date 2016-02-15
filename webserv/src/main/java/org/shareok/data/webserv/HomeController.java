/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

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
   public ModelAndView home() {
        ModelAndView model = new ModelAndView();
        model.setViewName("home");
        //model.addObject("message", "Hello Spring MVC Framework!");

        return model;
   }
}
