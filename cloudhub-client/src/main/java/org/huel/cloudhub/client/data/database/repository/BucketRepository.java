package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.BucketDao;
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

    public BucketRepository(CloudhubDatabase database) {
        this.bucketDao = database.getBucketDao();
    }

    public boolean isExitByName(String name){
        return bucketDao.getBucketNameByName(name) !=null;
    }

    public boolean isExitByUserId(long id){
        return bucketDao.getBucketNamesByUserId(id) !=null;
    }

    @Async //异步方法
    public void save(Bucket bucket) {
        insertOrUpdate(bucket);
    }

    private void insertOrUpdate(Bucket bucket) {
        if (isExitByName(bucket.getName())) {
           bucketDao.update(bucket);
            return;
        }
        bucketDao.insert(bucket);
    }

    public void insert(Bucket bucket){
        bucketDao.insert(bucket);
    }

    public void update(Bucket bucket){
        bucketDao.update(bucket);
    }

    public void deleteByName(String name){
        bucketDao.deleteByName(name);
    }

    public void delete(List<Bucket> buckets){
        bucketDao.delete(buckets);
    }

    public Bucket getBucketByName(String name){
        return bucketDao.getBucketByName(name);
    }

    public List<Bucket> getBucketUserId(long id){
        return bucketDao.getBucketsByUserId(id);
    }

    public String getBucketNameByName(String name){
        return bucketDao.getBucketNameByName(name);
    }


}
