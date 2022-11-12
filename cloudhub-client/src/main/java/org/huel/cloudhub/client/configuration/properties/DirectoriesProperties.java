package org.huel.cloudhub.client.configuration.properties;

/**
 * @author RollW
 */
public class DirectoriesProperties {
    private String tempDirectory;

    private String cacheDirectory;

    public String getTempDirectory() {
        return tempDirectory;
    }

    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }
}
