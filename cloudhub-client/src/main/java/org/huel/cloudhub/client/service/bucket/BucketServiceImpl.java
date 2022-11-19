package org.huel.cloudhub.client.service.bucket;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.client.data.database.repository.BucketRepository;
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
public class BucketServiceImpl implements BucketService{

    private final BucketRepository bucketRepository;

    public BucketServiceImpl(BucketRepository bucketRepository){
        this.bucketRepository = bucketRepository;
    }

    @Override
    public MessagePackage<BucketInfo> createBucket(long userId, String bucketName, BucketVisibility visibility) {
        Validate.notNull(userId,"userID cannot be null or empty.");
        Validate.notEmpty(bucketName,"bucketName cannot be null or empty");
        Validate.isTrue(visibility.isNeedAuth());

        if (bucketRepository.isExitByName(bucketName)){
            return new MessagePackage<>(ErrorCode.ERROR_DATA_EXISTED,"bucket exited" ,null);
        }
        if (bucketRepository.isExitByUserId(userId)){
            return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_EXIST,
                    "User not exist.", null);
        }
//        省略了桶权限
        Bucket bucket = new Bucket(bucketName,userId,System.currentTimeMillis(),visibility);
        bucketRepository.save(bucket);

        return new MessagePackage<>(ErrorCode.SUCCESS, bucket.toInfo());
    }


    @Override
    public List<Bucket> getUserBuckets(long userId) {
        return bucketRepository.getBucketUserId(userId);
    }

    @Override
    public Bucket queryByName(String name) {
        return bucketRepository.getBucketByName(name);
    }

    @Override
    public MessagePackage<Void> deleteBucket(long userId, String bucketName) {
        if (bucketRepository.isExitByUserId(userId)){
            bucketRepository.deleteByName(bucketName);
            return new MessagePackage<>(ErrorCode.SUCCESS,null);
        }

        return new MessagePackage<>(ErrorCode.ERROR_USER_NOT_EXIST,
                "User not exist.", null);
    }

    @Override
    public MessagePackage<Void> deleteBucketByName(String name) {
        if (bucketRepository.isExitByName(name)){
            bucketRepository.deleteByName(name);
            return new MessagePackage<>(ErrorCode.SUCCESS,null);
        }
        return new MessagePackage<>(ErrorCode.ERROR_NULL,"Bucket is not exist",null);
    }

    @Override
    public MessagePackage<BucketInfo> setVisibility(String name, BucketVisibility bucketVisibility) {
        Bucket bucket = bucketRepository.getBucketByName(name);
        if (bucket == null){
            return new MessagePackage<>(ErrorCode.ERROR_NULL,"Bucket is not exist",null);
        }

        bucket.setBucketVisibility(bucketVisibility);
        bucketRepository.save(bucket);
        return new MessagePackage<>(ErrorCode.SUCCESS, bucket.toInfo());
    }


}
