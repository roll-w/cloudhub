package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.database.repository.FileReferenceRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class FileReferenceService implements
        ObjectRemoveHandler, ObjectChangeActionHandler {
     private final FileReferenceRepository referenceRepository;

    public FileReferenceService(FileReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    @Override
    public void onAddNewObject(ObjectInfoDto objectInfoDto) {

    }

    @Override
    public void onObjectRename(ObjectInfo oldInfo, String newName) {

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
}
