package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface ByStatusProvider {
    void setCheckDeleted(boolean checkDeleted);

    boolean isCheckDeleted();
}
