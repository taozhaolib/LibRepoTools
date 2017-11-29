/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oulib.aws.s3;

/**
 *
 * @author zhao0677
 */
public class Dissertation {
    private String name;
    private String bucket;
    private String[] files;
    private String metadata;

    public String getName() {
        return name;
    }

    public String getBucket() {
        return bucket;
    }

    public String[] getFiles() {
        return files;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
}
