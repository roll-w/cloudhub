package org.huel.cloudhub.client.disk.domain.user;

/**
 * @author RollW
 */
public interface AttributedUser extends UserIdentity {
    String getNickname();

    boolean isEnabled();

    boolean isLocked();

    boolean isCanceled();

    long getCreateTime();

    long getUpdateTime();
}
