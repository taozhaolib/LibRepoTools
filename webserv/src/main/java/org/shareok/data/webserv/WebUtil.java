/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.FileCopyUtils;

/**
 *
 * @author Tao Zhao
 */
public class WebUtil {
    public static void setupFileDownload(HttpServletResponse response, String downloadPath){
        try{
            File file = new File(downloadPath);
            if(!file.exists()){
                 String errorMessage = "Sorry. The file you are looking for does not exist";
                 System.out.println(errorMessage);
                 OutputStream outputStream = response.getOutputStream();
                 outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
                 outputStream.close();
                 return;
            }

            String mimeType= URLConnection.guessContentTypeFromName(file.getName());
            if(mimeType==null){
                System.out.println("mimetype is not detectable, will take default");
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser 
                while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() +"\""));


            /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

            response.setContentLength((int)file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
        catch(IOException ioex){
            Logger.getLogger(JournalDataController.class.getName()).log(Level.SEVERE, null, ioex);
        }
    }
}
