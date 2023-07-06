package org.huel.cloudhub.client.disk.domain.userstorage.repository;

import org.huel.cloudhub.client.disk.domain.systembased.ContextThread;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.userstorage.*;
import org.huel.cloudhub.web.data.page.Offset;
import org.springframework.stereotype.Repository;
import space.lingu.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author RollW
 */
@Repository
public class UserStorageSearchRepoImpl implements UserStorageSearchRepository, UserStorageCompositeRepository {
    private final UserFileStorageRepository userFileStorageRepository;
    private final UserFolderRepository userFolderRepository;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;

    public UserStorageSearchRepoImpl(UserFileStorageRepository userFileStorageRepository,
                                     UserFolderRepository userFolderRepository,
                                     ContextThreadAware<PageableContext> pageableContextThreadAware) {
        this.userFileStorageRepository = userFileStorageRepository;
        this.userFolderRepository = userFolderRepository;
        this.pageableContextThreadAware = pageableContextThreadAware;
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

    @Override
    public List<AttributedStorage> listStorages() {
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        if (contextThread.hasContext()) {
            // return getAllStorages();
            return List.of();
        }
        // TODO: current not support
        return List.of();
    }

    private List<AttributedStorage> getAllStorages() {
        List<AttributedStorage> result = new ArrayList<>();
        result.addAll(userFileStorageRepository.get());
        result.addAll(userFolderRepository.get());
        return result;
    }

    private List<AttributedStorage> getAllStorages(StorageOwner storageOwner) {
        List<AttributedStorage> result = new ArrayList<>();
        result.addAll(userFileStorageRepository.getByOwner(storageOwner, null));
        result.addAll(userFolderRepository.getByOwner(storageOwner, null));
        return result;
    }


    @Override
    public List<AttributedStorage> listStorages(StorageOwner storageOwner) {
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        if (!contextThread.hasContext()) {
            return getAllStorages(storageOwner);
        }

        PageableContext context = contextThread.getContext();

        if (context.isIncludeDeleted()) {
            StorageOffset storageOffset = queryByConditions(
                    () -> userFolderRepository.countByOwner(storageOwner),
                    () -> userFileStorageRepository.countByOwner(storageOwner),
                    context
            );
            List<AttributedStorage> result = new ArrayList<>();
            if (storageOffset.folderOffset().limit() > 0) {
                result.addAll(
                        userFolderRepository.getByOwner(
                                storageOwner,
                                storageOffset.folderOffset())
                );
            }
            if (storageOffset.fileOffset().limit() > 0) {
                result.addAll(userFileStorageRepository.getByOwner(
                        storageOwner,
                        storageOffset.fileOffset())
                );
            }
            return result;
        }

        StorageOffset storageOffset = queryByConditions(
                () -> userFolderRepository.countActiveByOwner(storageOwner),
                () -> userFileStorageRepository.countActiveByOwner(storageOwner),
                context
        );
        List<AttributedStorage> result = new ArrayList<>();
        if (storageOffset.folderOffset().limit() > 0) {
            result.addAll(
                    userFolderRepository.getActiveByOwner(
                            storageOwner,
                            storageOffset.folderOffset())
            );
        }
        if (storageOffset.fileOffset().limit() > 0) {
            result.addAll(userFileStorageRepository.getActiveByOwner(
                    storageOwner,
                    storageOffset.fileOffset())
            );
        }
        return result;
    }

    private StorageOffset queryByConditions(
            Supplier<Integer> folderCountSupplier,
            Supplier<Integer> fileCountSupplier,
            PageableContext context
    ) {
        int folderCount = folderCountSupplier.get();
        int fileCount = fileCountSupplier.get();

        context.setTotal(folderCount + fileCount);

        int currentPageSize = context.getSize() * context.getPage();
        if (currentPageSize > folderCount) {
            int fileOffset = currentPageSize - folderCount;
            int folderSize = context.getSize() - fileOffset;
            int fileSize = context.getSize() - folderSize;

            return new StorageOffset(
                    new Offset(folderSize, (context.getPage() - 1) * context.getSize()),
                    new Offset(fileSize, 0)
            );
        }

        return new StorageOffset(
                context.toOffset(),
                new Offset(0, 0)
        );
    }

    private record StorageOffset(
            Offset folderOffset,
            Offset fileOffset
    ) {
    }
}
