/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import java.util.List;
import java.util.Map;
import org.shareok.data.kernel.api.services.DataService;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceRestService extends DataService {
    public String loadItemsFromSafPackage();
    public Map<String, String> getItemDoisByCollectionHandler(String handle, String dspaceApiUrl);
}
