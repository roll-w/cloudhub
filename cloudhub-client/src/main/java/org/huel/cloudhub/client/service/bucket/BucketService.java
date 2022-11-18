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
    // TODO: 提供给用户的桶管理服务
    MessagePackage<BucketInfo> createBucket(long userId, String bucketName,
                                            BucketVisibility visibility);

    MessagePackage<Void> deleteBucket(long userId, String bucketName);

    List<Bucket> getUserBuckets(long userId);
}
