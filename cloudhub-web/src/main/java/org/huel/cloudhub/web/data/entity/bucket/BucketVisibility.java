package org.huel.cloudhub.web.data.entity.bucket;

/**
 * 桶可见性。
 *
 * @author RollW
 */
public enum BucketVisibility {
    PUBLIC_READ(false),
    PUBLIC_READ_WRITE(false),
    PRIVATE(true);

    private final boolean needAuth;

    BucketVisibility(boolean needAuth) {
        this.needAuth = needAuth;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }
}