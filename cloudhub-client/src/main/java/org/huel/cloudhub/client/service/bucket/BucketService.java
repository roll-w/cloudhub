package org.huel.cloudhub.client.service.bucket;

import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.bucket.BucketVisibility;
import org.huel.cloudhub.common.MessagePackage;

import java.util.List;

/**
 * @author RollW
 */
public interface BucketService {
    MessagePackage<BucketInfo> createBucket(long userId, String bucketName,
                                            BucketVisibility visibility);

    MessagePackage<Void> deleteBucket(long userId, String bucketName);

    MessagePackage<Void> deleteBucket(String bucketName);

    List<BucketInfo> getUserBuckets(long userId);

    List<BucketInfo> getAllUsersBuckets();

    Bucket getBucketByName(String name);

    MessagePackage<BucketInfo> setVisibility(String name, BucketVisibility bucketVisibility);

}
