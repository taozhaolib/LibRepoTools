/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.job;

/**
 *
 * @author Tao Zhao
 */
public interface RepositoryJobService {
    public void executeJob(long uid, int jobType, int repoType);
}
