package org.huel.cloudhub.client.data.entity.bucket;

/**
 * 桶可见性（桶策略）。
 *
 * @author RollW
 */
public enum BucketVisibility {
    PUBLIC_READ(false, true),
    PUBLIC_READ_WRITE(false, false),
    PRIVATE(true, true);

    private final boolean needReadAuth;
    private final boolean needWriteAuth;

    BucketVisibility(boolean needReadAuth, boolean needWriteAuth) {
        this.needReadAuth = needReadAuth;
        this.needWriteAuth = needWriteAuth;
    }

    public boolean isNeedReadAuth() {
        return needReadAuth;
    }

    public boolean isNeedWriteAuth() {
        return needWriteAuth;
    }
}