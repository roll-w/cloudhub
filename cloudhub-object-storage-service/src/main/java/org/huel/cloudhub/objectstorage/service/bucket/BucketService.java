package org.huel.cloudhub.objectstorage.service.bucket;

import org.huel.cloudhub.objectstorage.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.objectstorage.data.entity.bucket.Bucket;
import org.huel.cloudhub.objectstorage.data.entity.bucket.BucketVisibility;

import java.util.List;

/**
 * @author RollW
 */
public interface BucketService {
    BucketInfo createBucket(long userId, String bucketName,
                            BucketVisibility visibility);

    void deleteBucket(long userId, String bucketName);

    void deleteBucket(String bucketName);

    List<BucketInfo> getUserBuckets(long userId);

    int getBucketsCount();

    List<BucketInfo> getAllUsersBuckets();

    Bucket getBucketByName(String name);

    BucketInfo setVisibility(String name, BucketVisibility bucketVisibility);

}
