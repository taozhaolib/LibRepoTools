/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceSafPackageDataService {
    public String[] getSafPackagePaths(String dataFolderPath);
    public String[] getSafPackagePaths(MultipartFile file);
}
