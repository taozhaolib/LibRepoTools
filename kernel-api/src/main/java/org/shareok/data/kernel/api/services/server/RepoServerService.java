/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.server;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.shareok.data.redis.server.RepoServer;

/**
 *
 * @author Tao Zhao
 */
public interface RepoServerService {
    public Map<String, String> getServerNameIdList();
    public RepoServer findServerById(int serverId);
//    public List<RepoServer> getServerObjList(Collection<String> serverIds);
//    public List<RepoServer> loadRepoServerListByRepoType(List<RepoServer> serverList);
}
