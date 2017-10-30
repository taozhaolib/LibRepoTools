/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services;

/**
 * main function argument data is a JSON String
 * Action type: 
 *       journal-search <journal articles search> : java -jar target/kernel-api-1.0-SNAPSHOT-jar-with-dependencies.jar id-0 journal-search '{"publisher" : "sage", "startDate": "2017-01-02", "endDate" : "2017-04-05", "affiliate" : "University of Oklahoma"}'
 *       journal-saf <journal articles SAF package> : java -jar target/kernel-api-1.0-SNAPSHOT-jar-with-dependencies.jar id-1 journal-saf '{"dois": "10.1371/journal.pone.0171910;10.1371/journal.pone.0171683;10.1371/journal.pone.0171193", "startDate" : "2017-03-03", "endDate" : "2017-03-28"}'
                                                      java -jar target/kernel-api-1.0-SNAPSHOT-jar-with-dependencies.jar id-1 journal-saf '{"dois": "10.1177/8755123315593325;10.1177/1461444815606121", "startDate" : "2017-03-03", "endDate" : "2017-03-28"}'
 *       journal-import <journal articles SAF import> : java -jar target/kernel-api-1.0-SNAPSHOT-jar-with-dependencies.jar sdfx7 journal-import '{"safPath" : "/var/local/librepotools/librepotools-data/uploads/2017.07.21.16.42.16/sage/output_sage_2017-03-03_2017-03-28.zip", "collectionHandle" : "11244/37263", "dspaceApiUrl" : "https://test.shareok.org/rest"}'
 *       saf-build <Build up a SAF package for DSpace ingest> : java -jar target/kernel-api-1.0.3-SNAPSHOT-jar-with-dependencies.jar id-saf-1 saf-build '{"csvPath" : "/Users/zhao0677/Projects/ouhj/OUHJ-1/ouhj2.csv"}'
 * 
 * @author Tao Zhao
 */
public class Main {
    public static void main(String[] args){        
        try {
            String taskId = args[0];
            String taskType = args[1];
            String data = args[2];
            ServiceUtil.executeCommandLineTask(taskId, taskType, data);
        } catch (Exception ex) {
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
