/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

import org.shareok.data.datahandlers.JobHandler;
import org.shareok.data.redis.job.RedisJob;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public interface TaskManager {
    public RedisJob execute(long uid, JobHandler handler, MultipartFile localFile, String remoteFilePath);
    public RedisJob execute(RedisJob job);
//    public RedisJob execute(long uid, String jobType, String repoType, TaskManager handler, MultipartFile localFile, String remoteFilePath);
}
