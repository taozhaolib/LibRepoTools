/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.redis.server;

import org.springframework.data.redis.core.BoundHashOperations;

/**
 *
 * @author Tao Zhao
 */
public interface RepoTypeServerDao {
    public RepoServer loadServerParametersByRepoType(RepoServer server, BoundHashOperations<String, String, String> serverOps);
    public RepoServer getRepoTypeServerFromAbstract(RepoServer server);
}
