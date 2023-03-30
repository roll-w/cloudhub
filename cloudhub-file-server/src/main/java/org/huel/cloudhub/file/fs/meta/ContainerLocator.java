package org.huel.cloudhub.file.fs.meta;

/**
 * @author RollW
 */
public interface ContainerLocator {
    String getLocator();

    long getSerial();

    long getVersion();

    String getSource();
}
