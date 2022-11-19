package org.huel.cloudhub.client.service.bucket;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.data.database.repository.BucketRepository;
import org.huel.cloudhub.client.data.database.repository.UserRepository;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.bucket.BucketVisibility;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cheng
 */
@Service
public class BucketServiceImpl implements BucketService {

    private final BucketRepository bucketRepository;
    private final UserRepository userRepository;

    public BucketServiceImpl(BucketRepository bucketRepository,
                             UserRepository userRepository) {
        this.bucketRepository = bucketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MessagePackage<BucketInfo> createBucket(long userId, String bucketName,
                                                   BucketVisibility visibility) {
        Validate.notEmpty(bucketName, "bucketName cannot be null or empty");
        Validate.notNull(visibility, "bucketVisibility cannot be null");

        if (bucketRepository.isExistByName(bucketName)) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_EXISTED,
                    "bucket exited", null);
        }
        if (userRepository.isExistById(userId)) {
            return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exist.", null);
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

    public Bucket getBucketByName(String name) {
        return bucketRepository.getBucketByName(name);
    }

    @Override
    public MessagePackage<Void> deleteBucket(long userId, String bucketName) {
        if (!userRepository.isExistById(userId)) {
            return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exist.", null);
        }
        Bucket bucket = bucketRepository.getBucketByName(bucketName);
        if (bucket == null) {
            return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                    "Bucket not exist.", null);
        }
        if (bucket.getUserId() != userId) {
            return new MessagePackage<>(ErrorCode.ERROR_PERMISSION_NOT_ALLOWED,
                    "You are not permitted.", null);
        }
        bucketRepository.delete(bucket);
        return new MessagePackage<>(ErrorCode.SUCCESS, null);
    }

    @Override
    public MessagePackage<Void> deleteBucket(String bucketName) {
        if (bucketRepository.isExistByName(bucketName)) {
            bucketRepository.deleteByName(bucketName);
            return new MessagePackage<>(ErrorCode.SUCCESS, null);
        }
        return new MessagePackage<>(ErrorCode.ERROR_DATA_NOT_EXIST,
                "Bucket not exist.", null);
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
}
