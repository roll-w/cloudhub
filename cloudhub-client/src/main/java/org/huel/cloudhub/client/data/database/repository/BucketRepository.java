package org.huel.cloudhub.client.data.database.repository;

import org.huel.cloudhub.client.data.database.CloudhubDatabase;
import org.huel.cloudhub.client.data.database.dao.BucketDao;
import org.springframework.stereotype.Repository;

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
        return bucketDao.getBucketByName(name) !=null;
    }

}
