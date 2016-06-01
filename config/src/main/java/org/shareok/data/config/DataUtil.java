/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.config;

/**
 *
 * @author Tao Zhao
 */
public class DataUtil {
    public static String[] REPO_TYPES = {"unknown", "dspace", "islandora", "fedora", "hydra"};
    public static String[] JOB_TYPES = {"unknown", "ssh-import", "rest-import", "ssh-upload", "ssh-import-uloaded"};
    
    public static int getJobTypeIndex(String jobType){
        if(null == jobType || "".equals(jobType)){
            return 0;
        }
        else if(jobType.equals("import")){
            return 1;
        }
        else if(jobType.equals("upload")){
            return 3;
        }
        else if(jobType.equals("import-uploaded")){
            return 4;
        }
        /**
         * Some other should also be implemented here.
         */
        else {
            return 0;
        }
    }
    
    public static int getRepoTypeIndex(String repoType){
        if(null == repoType || "".equals(repoType)){
            return 0;
        }
        else if(repoType.equals("dspace")){
            return 1;
        }
        else if(repoType.equals("islandora")){
            return 2;
        }
        else if(repoType.equals("fedora")){
            return 3;
        }
        else if(repoType.equals("hydra")){
            return 2;
        }
        /**
         * Some other should also be implemented here.
         */
        else {
            return 0;
        }
    }
}
