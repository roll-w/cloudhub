package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFolder;
import org.springframework.stereotype.Repository;
import space.lingu.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
@Repository
public class UserStorageSearchRepoImpl implements UserStorageSearchRepository {
    private final UserFileStorageRepository userFileStorageRepository;
    private final UserFolderRepository userFolderRepository;

    public UserStorageSearchRepoImpl(UserFileStorageRepository userFileStorageRepository,
                                     UserFolderRepository userFolderRepository) {
        this.userFileStorageRepository = userFileStorageRepository;
        this.userFolderRepository = userFolderRepository;
    }

    @Override
    public List<? extends AttributedStorage> findStoragesBy(
            @NonNull UserStorageSearchCondition userStorageSearchCondition) {
        StorageType storageType =
                userStorageSearchCondition.storageType();
        if (storageType == null && userStorageSearchCondition.fileType() == null) {
            return findStoragesByAll(userStorageSearchCondition);
        }
        if (storageType == null) {
            return findFileStorages(userStorageSearchCondition);
        }
        return switch (storageType) {
            case FILE -> findFileStorages(userStorageSearchCondition);
            case FOLDER -> findFolderStorages(userStorageSearchCondition);
            case LINK -> List.of();
        };
    }

    private List<? extends AttributedStorage> findStoragesByAll(
            UserStorageSearchCondition userStorageSearchCondition) {
        List<UserFileStorage> userFileStorages =
                findFileStorages(userStorageSearchCondition);
        List<UserFolder> userFolderStorages =
                findFolderStorages(userStorageSearchCondition);
        List<AttributedStorage> result = new ArrayList<>(userFolderStorages);
        result.addAll(userFileStorages);

        return result;
    }

    private List<UserFileStorage> findFileStorages(
            UserStorageSearchCondition userStorageSearchCondition) {
        return userFileStorageRepository.findFilesByConditions(
                userStorageSearchCondition.storageOwner(),
                userStorageSearchCondition.name(),
                userStorageSearchCondition.fileType(),
                userStorageSearchCondition.before(),
                userStorageSearchCondition.after()
        );
    }

    private List<UserFolder> findFolderStorages(
            UserStorageSearchCondition userStorageSearchCondition) {
        return userFolderRepository.findFoldersByCondition(
                userStorageSearchCondition.storageOwner(),
                userStorageSearchCondition.name(),
                userStorageSearchCondition.before(),
                userStorageSearchCondition.after()
        );
    }
}
