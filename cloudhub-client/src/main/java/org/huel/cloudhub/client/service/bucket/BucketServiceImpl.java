package org.huel.cloudhub.client.service.bucket;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.data.database.repository.BucketRepository;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.bucket.BucketVisibility;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Cheng
 */
@Service
public class BucketServiceImpl implements BucketService {
    private static final Pattern sBucketNamePattern =
            Pattern.compile("^[A-Za-z0-9-]*$");

    private final BucketRepository bucketRepository;

    public BucketServiceImpl(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    @Override
    public MessagePackage<BucketInfo> createBucket(long userId, String bucketName,
                                                   BucketVisibility visibility) {
        Validate.notEmpty(bucketName, "bucketName cannot be null or empty");
        Validate.notNull(visibility, "bucketVisibility cannot be null");
        if (!sBucketNamePattern.matcher(bucketName).matches()) {
            throw new BucketRuntimeException(
                    ErrorCode.ERROR_ILLEGAL_PARAM, "Illegal bucket name.");
        }
        if (bucketRepository.isExistByName(bucketName)) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_EXISTED,
                    "Bucket exited.", null);
        }
        Bucket bucket = new Bucket(bucketName, userId,
                System.currentTimeMillis(), visibility);
        bucketRepository.save(bucket);

        return new MessagePackage<>(ErrorCode.SUCCESS, bucket.toInfo());
    }

    @Override
    public List<BucketInfo> getUserBuckets(long userId) {
        return bucketRepository.getBucketInfosByUserId(userId);
    }

    @Override
    public List<BucketInfo> getAllUsersBuckets() {
        return bucketRepository.getAllBucketInfos();
    }

    public Bucket getBucketByName(String name) {
        return bucketRepository.getBucketByName(name);
    }

    @Override
    public MessagePackage<Void> deleteBucket(long userId, String bucketName) {
        Bucket bucket = bucketRepository.getBucketByName(bucketName);
        if (bucket == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist.", null);
        }
        if (bucket.getUserId() != userId) {
            return new MessagePackage<>(ErrorCode.ERROR_PERMISSION_NOT_ALLOWED,
                    "You are not permitted.", null);
        }
        if (!bucketRepository.isBucketEmpty(bucketName)) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EMPTY,
                    "Bucket not empty, cannot delete", null);
        }
        bucketRepository.deleteByName(bucketName);
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public MessagePackage<Void> deleteBucket(String bucketName) {
        if (!bucketRepository.isExistByName(bucketName)) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist.", null);
        }
        if (!bucketRepository.isBucketEmpty(bucketName)) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EMPTY,
                    "Bucket not empty, cannot delete", null);
        }
        bucketRepository.deleteByName(bucketName);
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public MessagePackage<BucketInfo> setVisibility(String name, BucketVisibility bucketVisibility) {
        Bucket bucket = bucketRepository.getBucketByName(name);
        if (bucket == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist.", null);
        }

        bucket.setBucketVisibility(bucketVisibility);
        bucketRepository.save(bucket);
        return new MessagePackage<>(ErrorCode.SUCCESS, bucket.toInfo());
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
        if (s) {
            return BucketControlCode.ALLOW;
        }
        return BucketControlCode.DENIED;
    }

    private boolean authUserId(Long userId, long expect) {
        if (userId == null) {
            return false;
        }
        return userId == expect;
    }

}
