/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oulib.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import oulib.aws.s3.S3BookInfo;
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
            ClientConfiguration config = null;
            try {
                credentials = new ProfileCredentialsProvider("default").getCredentials();
                config = new ClientConfiguration();
                config.setConnectionTimeout(250000);
                config.setSocketTimeout(250000);
            } catch (Exception e) {
                throw new AmazonClientException(
                        "Cannot load the credentials from the credential profiles file. " +
                                "Please make sure that your credentials file is at the correct " +
                                "location (/Users/zhao0677/.aws/credentials), and is in valid format.",
                        e);
            }
            
            AmazonS3 s3client = new AmazonS3Client(credentials, config);
            Region usEast = Region.getRegion(Regions.US_EAST_1);
            s3client.setRegion(usEast);
            
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
            //System.out.println("arg0 = "+args[0]+" arg1 = "+args[1]+" arg2 = "+args[2]+ " arg3 = "+args[3]);
            ExecutorService executor = Executors.newFixedThreadPool(threadMaxCount);
            List<String> tiffDiff = S3Util.getS3BucketFolderObjDiff(s3client, "ul-bagit", bookName+"/data", "ul-ir-workspace", bookName+"/data");
            int diff = tiffDiff.size();
            if(diff > 0){
                System.out.println("There are totally "+String.valueOf(diff)+" tiff images to process.\nStart processing at "+(new java.util.Date()).toString());
                AwsDataProcessorThreadFactory threadFactory = new AwsDataProcessorThreadFactory();                
                for(int i = 0; i <= 10; i++){
                    S3TiffProcessorThread s3TiffProcessorThread = new S3TiffProcessorThread(s3client, bookInfo, String.valueOf(i)+".tif", tiffDiff);
                    threadFactory.setIndex(i);
                    threadFactory.setJobType("small-tiff-" + bookName);
                    executor.execute(threadFactory.newThread(s3TiffProcessorThread));
                }
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("All the derivatives were generated at "+(new java.util.Date()).toString()+"!");
//            System.out.println("size = "+tiffDiff.size());
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "1.tif", tiffDiff), "thread 1").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "2.tif", tiffDiff), "thread 2").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "3.tif", tiffDiff), "thread 3").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "4.tif", tiffDiff), "thread 4").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "5.tif", tiffDiff), "thread 5").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "6.tif", tiffDiff), "thread 6").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "7.tif", tiffDiff), "thread 7").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "8.tif", tiffDiff), "thread 8").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "9.tif", tiffDiff), "thread 9").start();
//            new Thread(new S3TiffProcessorThread(s3client, bookInfo, "0.tif", tiffDiff), "thread 10").start();
//            System.out.println("There are "+tiffDiff.size()+" items different.");
//            Iterator<String> it = tiffDiff.iterator();
//            while(it.hasNext()){
//                System.out.println("File = "+it.next());
//            }
//            S3Util.generateTifDerivativesByS3Bucket(s3client, bookInfo);
            
//            S3Object obj1 = s3client.getObject("ul-bagit", "Alberti_1568/data/001.tif");
//            S3Object obj2 = s3client.getObject("ul-ir-workspace", "Alberti_1568/data/067.tif");
//            S3Util.generateSmallTiff(s3client, obj1, "ul-ir-workspace", S3Util.COMPRESSION_RATE_75_PERCENT_OF_ORIGINAL);
//            S3Util.generateSmallTiffWithTargetSize(s3client, obj1, "ul-ir-workspace", S3Util.COMPRESSOIN_TARGET_SIZE_EXTRA_SMALL);
//            S3Util.copyS3ObjectTiffMetadata(s3client, obj1, s3client.getObject(new GetObjectRequest("ul-ir-workspace", obj1.getKey())), "ul-ir-workspace", obj1.getKey()+".tif");
//            S3Util.copyS3ObjectTiffMetadata(s3client, obj1, obj2, "ul-ir-workspace", "Alberti_1568/data/067.tif.tif");
            
        } catch (Exception ex) {
            ex.printStackTrace();//logger.error("Cannot finish generating the small tiff images" + ex.getMessage());
        }
    }
}
