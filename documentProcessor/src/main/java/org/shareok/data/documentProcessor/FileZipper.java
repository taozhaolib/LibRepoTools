/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.documentProcessor;

/**
 *
 * @author Tao Zhao
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.shareok.data.documentProcessor.exceptions.EmptyFilePathException;
import org.shareok.data.documentProcessor.exceptions.FileTypeException;

public class FileZipper {
    
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FileZipper.class);
    private static final int BUFFER_SIZE = 4096;

    public static void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }

    private static void addFileToZip(String path, String srcFile, ZipOutputStream zip)
       throws Exception {

     File folder = new File(srcFile);
     if (folder.isDirectory()) {
       addFolderToZip(path, srcFile, zip);
     } else {
       byte[] buf = new byte[1024];
       int len;
       FileInputStream in = new FileInputStream(srcFile);
       zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
       while ((len = in.read(buf)) > 0) {
         zip.write(buf, 0, len);
       }
     }
   }

    private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);

        for (String fileName : folder.list()) {
          if (path.equals("")) {
            addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
          } else {
            addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
          }
        }
    }
   
    public static String unzipToDirectory(String zipPath){
        File zip = new File(zipPath);
        return unzipToDirectory(zipPath, zip.getParent());
    }
  
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipPath
     * @param outputDir
     * @return path to the new unzipped file
     * @throws IOException
     */
    public static String unzipToDirectory(String zipPath, String outputDir){
        
        String newFilePath = null; 
        String fileName = null;
        ZipInputStream zipIn = null;
        
        try{
            File destDir = new File(outputDir);
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            zipIn = new ZipInputStream(new FileInputStream(zipPath));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = outputDir + File.separator + entry.getName();
                if(null == fileName){
                    fileName = filePath;
                }
                if(filePath.contains("MACOSX")){
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                    continue;
                }
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }            
        }
        catch(IOException ex){
            logger.error("Error after Zipping file: " + ex.getMessage());
        }
        finally{
            if(null != zipIn){
                try{
                    zipIn.close();
                }
                catch(IOException ex){
                    logger.error("Cannot close ZipInputStream after Zipping file: " + ex.getMessage());
                }
            }
        }
        return fileName;
    }
    
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) {
        BufferedOutputStream bos = null;
        try{
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
        catch(IOException ex){
            logger.error("Cannot close BufferedOutputStream after Zipping file: " + ex.getMessage());
        }
        finally{
            if(null != bos){
                try{
                    bos.close();
                }
                catch(IOException ex){
                    logger.error("Cannot close BufferedOutputStream after Zipping file: " + ex.getMessage());
                }
            }
        }
    }
}
