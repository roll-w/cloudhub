package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.BucketDao;
import org.huel.cloudhub.client.data.database.dao.FileObjectStorageDao;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Cheng
 */
@Repository
public class BucketRepository {
    private final BucketDao bucketDao;
    private final FileObjectStorageDao fileObjectStorageDao;

    public BucketRepository(CloudhubDatabase database) {
        this.bucketDao = database.getBucketDao();
        this.fileObjectStorageDao = database.getFileObjectStorageDao();
    }

    public boolean isExistByName(String name) {
        return bucketDao.getBucketNameByName(name) != null;
    }

    @Async //异步方法
    public void save(Bucket bucket) {
        insertOrUpdate(bucket);
    }

    private void insertOrUpdate(Bucket bucket) {
        if (isExistByName(bucket.getName())) {
            bucketDao.update(bucket);
            return;
        }
        bucketDao.insert(bucket);
    }

    public void insert(Bucket bucket) {
        bucketDao.insert(bucket);
    }

    public void update(Bucket bucket) {
        bucketDao.update(bucket);
    }

    public void deleteByName(String name) {
        bucketDao.deleteByName(name);
    }

    public void delete(List<Bucket> buckets) {
        bucketDao.delete(buckets);
    }

    public void delete(Bucket bucket) {
        bucketDao.delete(bucket);
    }

    public Bucket getBucketByName(String name) {
        return bucketDao.getBucketByName(name);
    }

    public List<Bucket> getBucketUserId(long id) {
        return bucketDao.getBucketsByUserId(id);
    }

    public List<BucketInfo> getBucketInfosByUserId(long id) {
        return bucketDao.getBucketInfosByUserId(id);
    }

    public List<BucketInfo> getAllBucketInfos() {
        return bucketDao.getBucketInfos();
    }

    public String getBucketNameByName(String name) {
        return bucketDao.getBucketNameByName(name);
    }

    public boolean isBucketEmpty(String bucketName) {
        return fileObjectStorageDao.getBucketHasObjects(bucketName) == null;
    }

    public int getBucketsCount() {
        return bucketDao.getBucketCount();
    }
}
