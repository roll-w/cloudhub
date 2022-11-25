package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.database.repository.VersionedObjectRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class VersionedObjectServiceImpl implements VersionedObjectService,
        ObjectRemoveHandler, ObjectChangeActionHandler {
    private final VersionedObjectRepository versionedObjectRepository;

    public VersionedObjectServiceImpl(VersionedObjectRepository versionedObjectRepository) {
        this.versionedObjectRepository = versionedObjectRepository;
    }

    @Override
    public void handleObjectRemove(ObjectInfoDto objectInfoDto) {

    }

    @Override
    public void handleObjectRemove(List<ObjectInfoDto> objectInfoDtos) {

    }

    @Override
    public void handleBucketDelete(String bucketName) {

    }

    @Override
    public void onAddNewObject(ObjectInfoDto objectInfoDto) {

    }

    @Override
    public void onObjectRename(ObjectInfo oldInfo, String newName) {

    }
}
