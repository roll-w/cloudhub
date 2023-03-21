package org.huel.cloudhub.client.service.bucket;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.data.database.repository.BucketRepository;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.bucket.BucketVisibility;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Cheng
 */
@Service
public class BucketServiceImpl implements BucketService, BucketAuthService {
    private static final Pattern sBucketNamePattern =
            Pattern.compile("^[A-Za-z0-9-]*$");

    private final BucketRepository bucketRepository;

    public BucketServiceImpl(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    @Override
    public BucketInfo createBucket(long userId, String bucketName,
                                   BucketVisibility visibility) {
        Validate.notEmpty(bucketName, "bucketName cannot be null or empty");
        Validate.notNull(visibility, "bucketVisibility cannot be null");
        if (!sBucketNamePattern.matcher(bucketName).matches()) {
            throw new BucketRuntimeException(WebCommonErrorCode.ERROR_PARAM_FAILED);
        }
        if (bucketRepository.isExistByName(bucketName)) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_EXISTED);
        }
        Bucket bucket = new Bucket(bucketName, userId,
                System.currentTimeMillis(), visibility);
        bucketRepository.save(bucket);
        return bucket.toInfo();
    }

    @Override
    public List<BucketInfo> getUserBuckets(long userId) {
        return bucketRepository.getBucketInfosByUserId(userId);
    }

    @Override
    public int getBucketsCount() {
        return bucketRepository.getBucketsCount();
    }

    @Override
    public List<BucketInfo> getAllUsersBuckets() {
        return bucketRepository.getAllBucketInfos();
    }

    public Bucket getBucketByName(String name) {
        return bucketRepository.getBucketByName(name);
    }

    @Override
    public void deleteBucket(long userId, String bucketName) {
        Bucket bucket = bucketRepository.getBucketByName(bucketName);
        if (bucket == null) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EXIST);
        }
        if (bucket.getUserId() != userId) {
            throw new BucketRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
        if (!bucketRepository.isBucketEmpty(bucketName)) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EMPTY);
        }
        bucketRepository.deleteByName(bucketName);
    }

    @Override
    public void deleteBucket(String bucketName) {
        if (!bucketRepository.isExistByName(bucketName)) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EXIST);
        }
        if (!bucketRepository.isBucketEmpty(bucketName)) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EMPTY);
        }
        bucketRepository.deleteByName(bucketName);
    }

    @Override
    public BucketInfo setVisibility(String name, BucketVisibility bucketVisibility) {
        Bucket bucket = bucketRepository.getBucketByName(name);
        if (bucket == null) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EXIST);
        }
        bucket.setBucketVisibility(bucketVisibility);
        bucketRepository.save(bucket);
        return bucket.toInfo();
    }

    @Override
    public BucketControlCode allowWrite(UserInfo userInfo, String bucketName) {
        Bucket bucket = getBucketByName(bucketName);
        if (bucket == null) {
            return BucketControlCode.BUCKET_NOT_EXIST;
        }
        if (!bucket.getBucketVisibility().isNeedWriteAuth()) {
            return BucketControlCode.ALLOW;
        }
        if (userInfo == null) {
            return BucketControlCode.DENIED;
        }
        boolean s = authUserId(userInfo.id(), bucket.getUserId());
        if (s) {
            return BucketControlCode.ALLOW;
        }
        return BucketControlCode.DENIED;
    }

    @Override
    public BucketControlCode allowRead(UserInfo userInfo, String bucketName) {
        Bucket bucket = getBucketByName(bucketName);
        if (bucket == null) {
            return BucketControlCode.BUCKET_NOT_EXIST;
        }
        if (!bucket.getBucketVisibility().isNeedReadAuth()) {
            return BucketControlCode.ALLOW;
        }
        if (userInfo == null) {
            return BucketControlCode.DENIED;
        }
        boolean s = authUserId(userInfo.id(), bucket.getUserId());
        return s ? BucketControlCode.ALLOW : BucketControlCode.DENIED;
    }

    @Override
    public boolean isOwnerOf(UserInfo userInfo, String bucketName) {
        Bucket bucket = getBucketByName(bucketName);
        if (bucket == null) {
            return false;
        }
        if (userInfo == null) {
            return false;
        }
        return authUserId(userInfo.id(), bucket.getUserId());
    }

    private boolean authUserId(Long userId, long expect) {
        if (userId == null) {
            return false;
        }
        return userId == expect;
    }

}
