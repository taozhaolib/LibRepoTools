/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

import java.util.Map;
import org.shareok.data.redis.job.DspaceApiJob;
import org.shareok.data.redis.job.DspaceApiJobDaoImpl;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Tao Zhao
 */
public class DspaceApiJobServiceImpl extends RedisJobServiceImpl {
    
    @Autowired
    @Qualifier("dspaceApiJobDaoImpl")
    private DspaceApiJobDaoImpl dspaceApiJobDaoImpl;
    
    @Override
    public DspaceApiJob findJobByJobId(long jobId){
        return dspaceApiJobDaoImpl.findJobByJobId(jobId);
    }
    
    @Override
    public Map<String, String> getReportData(RedisJob job){
        return dspaceApiJobDaoImpl.getReportData((DspaceApiJob)job);
    }
}
