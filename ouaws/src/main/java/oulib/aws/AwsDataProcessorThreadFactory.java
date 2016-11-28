/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oulib.aws;

import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Tao Zhao
 */
public class AwsDataProcessorThreadFactory implements ThreadFactory {
    
    private String jobType;
    private int index;

    public String getJobType() {
        return jobType;
    }

    public int getIndex() {
        return index;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public String setThreadPrefix(){
        return jobType + "-" + String.valueOf(index) + "-";
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, setThreadPrefix());
    }
    
}
