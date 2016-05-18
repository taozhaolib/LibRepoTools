/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tao Zhao
 */
package org.shareok.data.webserv;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Configuration
@PropertySource("classpath:shareokdata-web.properties")
public class PlosWebController{
 
   //@RequestMapping(method = RequestMethod.GET)
//     @RequestMapping("/")
//   public ModelAndView printHome() {
//       ModelAndView model = new ModelAndView();
//       System.out.print("\n **** ok the program at least gets here\n");
//       model.setViewName("plosWeb");
//      model.addObject("message", "Hello Spring MVC Framework!");
//
//      return model;
//   }
    
    @Autowired
    private Environment env;
   
    @RequestMapping("/plos")
   public ModelAndView plosHello() {
        ModelAndView model = new ModelAndView();
        model.setViewName("plosDataUpload");
        model.addObject("message", "Hello Spring MVC Framework!");

        return model;
   }
   
   @RequestMapping(value="/plos/upload", method=RequestMethod.POST)
   public ModelAndView plosUpload(@RequestParam("file") MultipartFile file) {
       
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
 
                // Creating the directory to store file
//                String rootPath = System.getProperty("catalina.home");
//                File dir = new File(rootPath + File.separator + "tmpFiles");
//                if (!dir.exists())
//                    dir.mkdirs();
// 
//                // Create the file on server
//                File serverFile = new File(dir.getAbsolutePath()
//                        + File.separator + name);
//                BufferedOutputStream stream = new BufferedOutputStream(
//                        new FileOutputStream(serverFile));
//                stream.write(bytes);
//                stream.close();
 
//                logger.info("Server File Location="
//                        + serverFile.getAbsolutePath());
                System.out.println("The uploaded file name is " + file.getName()+"\n");
                ModelAndView model = new ModelAndView();
                model.setViewName("plosDataUpload");
                model.addObject("message", file.getOriginalFilename());

                return model;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        return null;
   }
}
