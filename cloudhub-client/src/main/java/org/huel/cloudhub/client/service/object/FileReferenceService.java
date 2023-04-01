package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.client.data.database.repository.FileReferenceRepository;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.client.data.entity.object.FileReference;
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
        FileReference reference = referenceRepository.getReference(
                objectInfoDto.fileId());
        if (reference != null) {
            reference.increase();
            referenceRepository.update(reference);
            return;
        }
        FileReference newReference = new FileReference(objectInfoDto.fileId(), 1);
        referenceRepository.insert(newReference);
    }

    @Override
    public void onObjectRename(ObjectInfo oldInfo, String newName) {

    }

    @Override
    public void handleObjectRemove(ObjectInfoDto objectInfoDto) {
        FileReference reference = referenceRepository.getReference(
                objectInfoDto.fileId());
        if (reference == null) {
            return;
        }
        reference.decrease();
        referenceRepository.update(reference);
    }

    @Override
    public void handleObjectRemove(List<ObjectInfoDto> objectInfoDtos) {
        objectInfoDtos.forEach(this::handleObjectRemove);
    }

    @Override
    public void handleBucketDelete(String bucketName) {
        // TODO:
    }
}
