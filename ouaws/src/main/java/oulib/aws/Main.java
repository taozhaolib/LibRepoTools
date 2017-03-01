/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oulib.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import oulib.aws.s3.S3BookInfo;
import oulib.aws.s3.S3TiffMetadataProcessorThread;
import oulib.aws.s3.S3TiffProcessorThread;
import oulib.aws.s3.S3Util;

/**
 *
 * @author Tao Zhao
 */
public class Main {
//    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Main.class);
    
    public static void main(String[] args){
        
        try {
            AWSCredentials credentials = null;            
            AmazonS3 s3Client = null;
//            args = new String[4];
//            args[0] = "ul-bagit";
//            args[1] = "ul-ir-workspace";
//            args[2] = "Borelli_1680-1681";
//            args[3] = "6";
            try {
                credentials = new ProfileCredentialsProvider("default").getCredentials();                   
            } catch (Exception e) {
                String access_key_id = null;
                String secret_key_id = null;
                String credentialInfo = AwsUtil.getAwsCredentials();                
                ObjectMapper mapper = new ObjectMapper();
                Map<String,String> credentialInfoMap = new HashMap<>();
                credentialInfoMap = mapper.readValue(credentialInfo, HashMap.class);
                for(String key : credentialInfoMap.keySet()){

                    if("AccessKeyId".equals(key)){
                        access_key_id = credentialInfoMap.get(key);
                    }
                    else if("SecretAccessKey".equals(key)){
                        secret_key_id = credentialInfoMap.get(key);
                    }
                }
//                System.out.println("access_key_id = "+access_key_id+" access_key_id = "+access_key_id);
                if(null != access_key_id && null != secret_key_id){
                    credentials = new BasicAWSCredentials(access_key_id, secret_key_id);
//                    s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
                }
                else{
                    throw new AmazonClientException(
                        "Cannot load the credentials from the credential information. " +
                                "Please make sure that your credentials file is at the correct, and is in valid format.",
                        e);
                }
            }
            
            ClientConfiguration config = new ClientConfiguration();
            config.setConnectionTimeout(250000);
            config.setSocketTimeout(50000);     
            
            s3Client = new AmazonS3Client(credentials, config);
            Region usEast = Region.getRegion(Regions.US_EAST_1);
            s3Client.setRegion(usEast);
            
            String bookName = args[2];
            
            S3BookInfo bookInfo = new S3BookInfo();
            bookInfo.setBookName(bookName);
            bookInfo.setBucketSourceName(args[0]);
            bookInfo.setBucketTargetName(args[1]);
            bookInfo.setCompressionSize(15000000);
            
            // *** Generate metadadta *****
            
//                S3Util.copyS3ObjectTiffMetadata(s3client, "ul-bagit", "ul-ir-workspace", "Zuniga_1591/data/004.tif", "Zuniga_1591/data/004.tif");
//                S3Util.copyS3ObjectTiffMetadata(s3client, "ul-bagit", "ul-ir-workspace", "Zuniga_1591/data/004.tif", "Zuniga_1591/data/004-20.tif");
//            S3Util.copyS3ObjectTiffMetadata(s3client, "ul-bagit", "ul-ir-workspace", "Zuniga_1591/data/004.tif", "Zuniga_1591/data/004-50.tif");
            
            // *** Generate small tiffs *****
            Integer threadMaxCount = 0;
            try{
                threadMaxCount = Integer.valueOf(args[3]);
            }
            catch(Exception ex){
                ex.printStackTrace();//logger.error("Cannot parse the thread count! "+ex.getMessage());
                return;
            }
            System.out.println("arg0 = "+args[0]+" arg1 = "+args[1]+" arg2 = "+args[2]+ " arg3 = "+args[3]);
            ExecutorService executor = Executors.newFixedThreadPool(threadMaxCount);
            List<String> tiffDiff = S3Util.getBucketObjectKeyList(bookInfo.getBucketSourceName(), args[2], s3Client);//.getS3BucketFolderObjDiff(s3Client, args[0], bookName+"/data", args[1], bookName+"/data");
            int diff = tiffDiff.size();
            if(diff > 0){
                System.out.println("There are totally "+String.valueOf(diff)+" tiff images to process.\nStart processing at "+(new java.util.Date()).toString());
                AwsDataProcessorThreadFactory threadFactory = new AwsDataProcessorThreadFactory();                
                for(int i = 0; i <= 10; i++){
//                    S3TiffProcessorThread s3TiffProcessorThread = new S3TiffProcessorThread(s3Client, bookInfo, String.valueOf(i)+".tif", tiffDiff);
//                    threadFactory.setIndex(i);
//                    threadFactory.setJobType("small-tiff-" + bookName);
//                    executor.execute(threadFactory.newThread(s3TiffProcessorThread));
//                    System.out.println("obj has path = "+bookInfo.getBucketSourceName() + tiffDiff.get(i));
                    S3TiffMetadataProcessorThread thread = new S3TiffMetadataProcessorThread(s3Client, bookInfo, String.valueOf(i)+".tif", tiffDiff);
                    threadFactory.setIndex(i);
                    threadFactory.setJobType("tiff-metadata-" + bookName);
                    executor.execute(threadFactory.newThread(thread));
                }
            }
            else{
                System.out.println("There are no tiff images to process");
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("All the derivatives were generated at "+(new java.util.Date()).toString()+"!");

        } catch (Exception ex) {
            ex.printStackTrace();//logger.error("Cannot finish generating the small tiff images" + ex.getMessage());
        }
    }
}
