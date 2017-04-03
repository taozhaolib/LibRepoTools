/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.shareok.data.config.DataUtil;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.kernel.api.exceptions.InvalidCommandLineArgumentsException;
import org.shareok.data.kernel.api.services.config.ConfigServiceImpl;
import org.shareok.data.kernel.api.services.user.PasswordAuthenticationServiceImpl;
import org.shareok.data.redis.RedisConfigImpl;
import org.shareok.data.redis.RedisUser;
import org.shareok.data.redis.UserDaoImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * main function argument data is a JSON String
 * Action type: 
 *       journal-search <journal articles search> : mvn exec:exec@journal-search -Ddata='{"journal-search": {"publisher" : "sage", "startDate": "2017-01-02", "endDate" : "2017-04-05", "affiliate" : "University of Oklahoma"}}'
 *       journal-saf <journal articles SAF package> : mvn exec:exec@journal-saf -Ddata='{"journal-saf" : {"dois": "10.1177/0884533617695244;10.1177/0897190017696951", "startDate" : "2017-03-03", "endDate" : "2017-03-28"}}'
 *       journal-saf-import <journal articles SAF import> : mvn exec:exec@journal-import -Ddata='{"journal-import" : {"safPath" : "/var/local/librepotools/librepotools-data/uploads/2017.03.30.16.48.58/sage/output_sage_2017-03-03_2017-03-28.zip", "collectionHandle" : "11244/37263", "dspaceApiUrl" : "https://test.shareok.org/rest"}}'
 * 
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){
        String data = args[0];
        System.out.println("data = "+data);
        try {
            ServiceUtil.executeCommandLineTask(data);
        } catch (InvalidCommandLineArgumentsException ex) {
            ex.printStackTrace();
        }
//        Map<String, String> map1 = new HashMap<>();
//        Map<String, String> map2 = new HashMap<>();
//        map1.put("one", "1");
//        map1.put("two", "2");
//        map2.put("three", "3");
//        map2.put("four", "4");
//        List<Map<String, String>> list = new ArrayList<>();
//        list.add(map2);
//        list.add(map1);
//        List<Integer> indexes = new ArrayList<>();
//        int count = 0;
//        for(Map<String, String> map : list){
//            if(map.containsKey("one")){
//                indexes.add(count);
//            }
//            count++;
//        }
//        for(int i : indexes){
//            list.remove(i);
//        }
//        String json = DataUtil.getJsonFromListOfMap(list);
//        
//        String actType = args[0];
//        String publisher = args[1];
//        String startDate = args[2];
//        String endDate = args[3];
//        String affiliate = args[4];
//
//        if(DocumentProcessorUtil.isEmptyString(publisher) || DocumentProcessorUtil.isEmptyString(publisher) || 
//                DocumentProcessorUtil.isEmptyString(publisher) || DocumentProcessorUtil.isEmptyString(publisher) ||
//                DocumentProcessorUtil.isEmptyString(actType) ){
//            try {
//                throw new InvalidCommandLineArgumentsException("Command line argument information is : \n" + "action = "+actType
//                        +"publisher = "+publisher+" startDate = "+startDate+" endDate="+endDate+" affiliate="+affiliate);
//            } catch (InvalidCommandLineArgumentsException ex) {
//                return;
//            }
//        }
//        
//        System.out.println("publisher = "+publisher+" startDate = "+startDate+" endDate="+endDate+" affiliate="+affiliate);
//        System.out.println("json = "+json);
//        ApplicationContext context = new ClassPathXmlApplicationContext("redisContext.xml");
//        UserDaoImpl jobDao = (UserDaoImpl)context.getBean("userDaoImpl");
//        for(long i = 18L; i <= 31L; i++){
//            RedisUser user = jobDao.findUserByUserId(i);
//            if(null == user){
//                continue;
//            }
//            String pw  = user.getPassword();
//    //        user.setPassword(pw);
//            if(!pw.startsWith("$31$")){
//                context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
//                PasswordAuthenticationServiceImpl authDao = (PasswordAuthenticationServiceImpl)context.getBean("passwordAuthenticationServiceImpl");
//                user.setPassword(authDao.hash("tao"));
//                jobDao.updateUser(user);
//            }
//        }
        
//      ApplicationContext context = new ClassPathXmlApplicationContext("kernelApiContext.xml");
//        PasswordAuthenticationServiceImpl authDao = (PasswordAuthenticationServiceImpl)context.getBean("passwordAuthenticationServiceImpl");  
//        System.out.println(" allow registration is "+String.valueOf(authDao.authenticate("tao", "$31$16$nVGYKnd_7rmPQBhFCqq0yjlQMGfSoMLPqabryCLMa48")));
//        System.out.println(" allow registration is "+authDao.hash("tao"));
//        String pattern = "\\.(\\s?)PLoS(\\s?)(.*)(\\d+)(\\((\\d+)\\):(\\s?)e(\\d+))";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher("Luo M, Taylor JM, Spriggs A, Zhang H, Wu X, Russell S, et al. (2011) A Genome-Wide Survey of Imprinted Genes in Rice Seeds Reveals Imprinting Primarily Occurs in the Endosperm. PLoS Genet 7(6): e1002125. doi:10.1371/journal.pgen.1002125");
//        if (m.find( )) {
//           System.out.println(m.group(0));
//        }
//        else{
//            System.out.println("No match found!");
//        }
//        String[] dois = {"10.1371/journal.ppat.1005945", "10.1371/journal.pone.0163330"};
//        ServiceUtil.generateDspaceSafPackagesByDois(dois);
        
    }
}
