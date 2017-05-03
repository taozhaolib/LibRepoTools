/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.datahandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.io.FilenameUtils;
import org.shareok.data.config.DataUtil;
import org.shareok.data.config.ShareokdataManager;
import static org.shareok.data.config.ShareokdataManager.getShareokdataPath;
import org.shareok.data.datahandlers.exceptions.InvalidDoiException;
import org.shareok.data.datahandlers.exceptions.SecurityFileDoesNotExistException;
import org.shareok.data.documentProcessor.CsvHandler;
import org.shareok.data.documentProcessor.DocumentProcessorUtil;
import org.shareok.data.documentProcessor.FileHandlerFactory;
import org.shareok.data.htmlrequest.HttpRequestHandler;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Tao Zhao
 */
public class DataHandlersUtil {
    
    public static String CURRENT_TASK_ID = "";
    public static String CURRENT_TASK_TYPE = "";
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DataHandlersUtil.class);
    
    public static String getJobReportPath(String jobType, long jobId){
        return getJobReportPath(jobType, String.valueOf(jobId));
    }
    
    public static String getJobReportPath(String jobType, String jobId){
        String shareokdataPath = getShareokdataPath();
        String repoType = jobType.split("-")[2];
        String filePath = shareokdataPath + File.separator + repoType;
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        filePath += File.separator + jobType;
        file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        filePath += File.separator + jobId;
        file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        return filePath;
    }
    
    public static String getJobReportFilePath(String jobType, long jobId){
        return getJobReportPath(jobType, jobId) + File.separator + String.valueOf(jobId) + "-report.txt";
    }
    
    public static String getJobReportFilePath(RedisJob job){
        return getJobReportPath(DataUtil.JOB_TYPES[job.getType()], job.getJobId()) + File.separator + String.valueOf(job.getJobId()) + "-report.txt";
    }
    
    public static String[] getRepoCredentials(String repoName) throws SecurityFileDoesNotExistException, IOException{
        String[] credentials = new String[2];
        String securityFilePath = ShareokdataManager.getSecurityFilePath();
        File securityFile = new File(securityFilePath);
        if(!securityFile.exists()){
            throw new SecurityFileDoesNotExistException("The security file does NOT exist!");
        }
        String content = new String(Files.readAllBytes(Paths.get(securityFilePath)));
        ObjectMapper mapper = new ObjectMapper();
        RepoCredential[] credentialObjects = mapper.readValue(content, RepoCredential[].class);
        for(RepoCredential credentialObj : credentialObjects){
            if(repoName.equals(credentialObj.getRepoName())){
                credentials[0] = credentialObj.getUserName();
                credentials[1] = credentialObj.getPassword();
            }
        }
        return credentials;
    }
    
    public static String getDomainNameFromUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
    
    public static String getItemInfoByDoi(String doi) throws InvalidDoiException{
        String response = null;
        ApplicationContext context = new ClassPathXmlApplicationContext("htmlRequestContext.xml");
        HttpRequestHandler handler = (HttpRequestHandler) context.getBean("httpRequestHandler");
        String url = "http://api.crossref.org/works/" + doi;
        response = handler.sendGet(url);
        if(null == response){
            throw new InvalidDoiException("Cannot get article information by this doi: "+doi+" from crossref!");
        }
        return response;
    }
    
    /**
     * Get the key from file name to retrieve the download path from database
     * 
     * @param fileName
     * @return : key
     */
    public static String getFileNameKeyForDownloadPath(String fileName){
        return FilenameUtils.removeExtension(fileName);
    }
    
    /**
     * Convert "yyyy-MM-dd'T'HH:mm:ss" to "yyyy-MM-dd"
     * @param dateTime : input date time
     * @return formatted dateTime
     */
    public static String convertPubTimeFormat(String dateTime) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        Date d = sdf.parse(dateTime);
        return output.format(d);
    }
    
    public static String convertFullMonthDateStringFormat(String date) throws ParseException{
        DateFormat fmt = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        Date d;
        try {
            d = fmt.parse(date);
        } catch (ParseException ex) {
            fmt = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            try {
                d = fmt.parse(date);
            } catch (ParseException ex1) {
                try{
                    fmt = new SimpleDateFormat("MMMM dd yyyy", Locale.US);
                    d = fmt.parse(date);
                }
                catch(ParseException ex2){
                    fmt = new SimpleDateFormat("dd MMMM, yyyy", Locale.US);
                    d = fmt.parse(date);
                }
            }
        }
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        return output.format(d);
    }
    
    public static String getCurrentTimeString(){
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }
    
    public static String getTimeString(Date time){
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(time);
    }
    
    /**
     * 
     * @param dateStr1 : yyyy-MM-dd
     * @param dateStr2 : yyyy-MM-dd
     * @return : is dateStr1 before dateStr2
     */
    public static Integer datesCompare(String dateStr1, String dateStr2){
        Integer isBefore = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(dateStr1);
            Date date2 = sdf.parse(dateStr2);
            isBefore = date1.compareTo(date2);
        } catch (ParseException ex) {
            logger.error(ex);
        }
        return isBefore;
    }
    
    /**
     * 
     * @param date : yyyy-MM-dd
     * @return : yyyy
     */
    public static String getYearFromSimpleDateString(String date){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = format.parse(date);
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            return df.format(newDate);
        } catch (ParseException ex) {
            logger.error("Cannot get year from date string", ex);
        }
        return null;
    }
        
    public static void writeCsvData(String csvFilePath, List<List<String>> data){
        FileWriter writer = null;
        try {
            String extension = DocumentProcessorUtil.getFileExtension(csvFilePath);
            CsvHandler handler = (CsvHandler)FileHandlerFactory.getFileHandlerByFileExtension(extension);
            writer = new FileWriter(csvFilePath);
            for(List<String> row : data){
                handler.writeCsvLine(writer, row);
            }
        } catch (IOException ex) {
            logger.error("Cannot write data into csv file at "+csvFilePath, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                logger.error("Cannot close the file write for csv file at "+csvFilePath, ex);
            }
        }
    }
        
    @Cacheable("taskOutputFileWriter")
    public static BufferedWriter getWriterForTaskOutputFile(String taskId, String taskType) throws IOException{
        String outputFilePath = ShareokdataManager.getDspaceCommandLineTaskOutputPath() + "_" + DataHandlersUtil.getCurrentTimeString()+"_"+taskType+"_"+taskId+".txt";
        File outputFile = new File(outputFilePath);
        if(!outputFile.exists()){
            outputFile.createNewFile();
        }
        return new BufferedWriter(new FileWriter(outputFilePath, true));
    }
    
    public static String getTaskFileFolderPath(String taskId, String taskType) throws IOException {
        String outputFileContainerPath = ShareokdataManager.getDspaceCommandLineTaskOutputPath() + File.separator + taskType + File.separator + taskId;
        File containerFile = new File(outputFileContainerPath);
        if(!containerFile.exists()){
            containerFile.mkdirs();
        }
        return outputFileContainerPath;
    }
    
    public static String getUserInputInfoFilePath(String taskId, String taskType) throws IOException {
        String outputFileContainerPath = getTaskFileFolderPath(taskId, taskType);
        File outputFile = new File(outputFileContainerPath + File.separator + "userInputInfo.txt");
        if(!outputFile.exists()){
            outputFile.createNewFile();
        }
        return outputFile.getAbsolutePath();
    }
}
