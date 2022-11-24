package org.huel.cloudhub.client.service.bucket;

import org.huel.cloudhub.client.data.dto.user.UserInfo;

/**
 * @author RollW
 */
public interface BucketAuthService {
    BucketControlCode allowWrite(UserInfo userInfo, String bucketName);

    BucketControlCode allowRead(UserInfo userInfo, String bucketName);

    boolean isOwnerOf(UserInfo userInfo, String bucketName);

    enum BucketControlCode {
        ALLOW(true),
        BUCKET_NOT_EXIST(false),
        DENIED(false);

        private final boolean success;

        BucketControlCode(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
