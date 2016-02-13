/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.kernel.api.services.dspace;

import java.io.File;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tao Zhao
 */
public interface DspaceSageService {
    String getSageDsapceLoadingFilesByExcel(String filePath);
    String getSageMetadataFilesByExcel(String filePath);
    String getSageDsapceLoadingFiles(MultipartFile file);
}
