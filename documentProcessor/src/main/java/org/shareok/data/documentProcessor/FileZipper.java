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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.shareok.data.documentProcessor.exceptions.EmptyFilePathException;
import org.shareok.data.documentProcessor.exceptions.FileTypeException;

public class FileZipper {

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
   
    public static void unzipToDirectory(String zipPath) throws FileTypeException, EmptyFilePathException{
        File zip = new File(zipPath);
        unzipToDirectory(zipPath, zip.getParent());
    }
  
    public static void unzipToDirectory(String zipPath, String outputPath) throws FileTypeException, EmptyFilePathException{
        
        File zipFile = new File(zipPath);
        if(!zipFile.exists()){
            throw new EmptyFilePathException("The zip file does not exist!");
        }
        
        File outputDir = new File(outputPath);
        if(outputDir.exists() && !outputDir.isDirectory()){
            throw new FileTypeException("The output path is not a directory!");
        }
        if(!outputDir.exists()){
            outputDir.mkdirs();
        }
        
        ZipInputStream zis = null; 
        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];
        
        try{
            zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){
                
                String fileName = ze.getName();
                File newFile = new File(outputPath + File.separator + fileName);

                new File(newFile.getParent()).mkdirs();

                fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        finally{
            try{
                if(null != fos){
                    fos.close();
                }
                if(null != zis){
                    zis.closeEntry();
                    zis.close();
                }                
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
