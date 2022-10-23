package org.huel.cloudhub.web.data.database.repository;

import org.huel.cloudhub.web.data.database.CloudhubDatabase;
import org.huel.cloudhub.web.data.database.dao.FileObjectStorageDao;
import org.huel.cloudhub.web.data.database.dao.UserGroupDao;
import org.huel.cloudhub.web.data.dto.UserUsageInfo;
import org.huel.cloudhub.web.data.entity.GroupedUser;
import org.huel.cloudhub.web.data.entity.UserGroupConfig;
import org.huel.cloudhub.web.data.entity.UserUploadFileStorage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author RollW
 */
@Repository
public class UserGroupRepository {
    private final FileObjectStorageDao fileObjectStorageDao;
    private final UserGroupDao userGroupConfigDao;

    public UserGroupRepository(CloudhubDatabase database) {
        this.fileObjectStorageDao = database.getFileObjectStorageDao();
        this.userGroupConfigDao = database.getUserGroupConfigDao();
    }


    public UserUsageInfo getUsageInfo(long userId) {
        GroupedUser groupedUser =
                userGroupConfigDao.getGroupedUserById(userId);
        UserGroupConfig config;
        if (groupedUser == null) {
            config = userGroupConfigDao.getGroupConfigByNameOrDefault("default");
        } else {
            config = userGroupConfigDao.getGroupConfigByNameOrDefault(groupedUser.getUserGroup());
        }
        List<UserUploadFileStorage> storages =
                fileObjectStorageDao.getUploadsByUserId(userId);
        int mbSize = countToMb(storages);
        return new UserUsageInfo(userId, config.getName(),
                mbSize, storages.size(),
                config.getMaxFileSize(), config.getMaxNum());
    }

    private int countToMb(List<UserUploadFileStorage> storages) {
        AtomicLong res = new AtomicLong();
        storages.forEach(userUploadImageStorage ->
                res.addAndGet(userUploadImageStorage.fileSize()));
        return (int) (res.get() / 1024 / 1024);
    }
}
