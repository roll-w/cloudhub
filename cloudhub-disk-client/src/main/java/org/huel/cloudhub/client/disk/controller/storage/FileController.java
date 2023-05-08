package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.FileStreamInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.FileType;
import org.huel.cloudhub.client.disk.domain.userstorage.Storage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageAction;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageActionService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorageService;
import org.huel.cloudhub.client.disk.domain.userstorage.UserStorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.FileStorageInfo;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageOwner;
import org.huel.cloudhub.client.disk.domain.userstorage.vo.StorageVo;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
@Api
public class FileController {
    private final UserFileStorageService userFileStorageService;
    private final UserStorageSearchService userStorageSearchService;
    private final StorageActionService storageActionService;

    public FileController(UserFileStorageService userFileStorageService,
                          UserStorageSearchService userStorageSearchService,
                          StorageActionService storageActionService) {
        this.userFileStorageService = userFileStorageService;
        this.userStorageSearchService = userStorageSearchService;
        this.storageActionService = storageActionService;
    }

    @BuiltinOperate(BuiltinOperationType.CREATE_FILE)
    @PutMapping("/{type}/{ownerId}/disk/{directory}/{fileName}")
    public HttpResponseEntity<StorageVo> uploadFile(@PathVariable("directory") Long directoryId,
                                                    @PathVariable("ownerId") Long ownerId,
                                                    @PathVariable("type") String type,
                                                    @PathVariable("fileName") String fileName,
                                                    @RequestPart(name = "file") MultipartFile file) throws IOException {
        ApiContextHolder.ApiContext context = ApiContextHolder.getContext();
        UserIdentity userIdentity = context.userInfo();

        if (userIdentity.getUserId() != ownerId) {
            throw new AuthenticationException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }

        LegalUserType legalUserType = LegalUserType.from(type);
        StorageOwner storageOwner = new SimpleStorageOwner(ownerId, legalUserType);
        FileStorageInfo fileStorageInfo = new FileStorageInfo(
                fileName, directoryId, storageOwner);
        String mimeType = file.getContentType();

        FileStreamInfo fileStreamInfo = new FileStreamInfo(
                file.getInputStream(),
                mimeType,
                FileType.fromMimeType(mimeType)
        );
        Storage storage = userFileStorageService.uploadFile(
                fileStorageInfo, fileStreamInfo);

        return HttpResponseEntity.success(StorageVo.from(storage, 0));
    }

    @BuiltinOperate(BuiltinOperationType.CREATE_FILE)
    @PostMapping("/{type}/{ownerId}/disk/{directory}")
    public HttpResponseEntity<StorageVo> uploadFile(
            @PathVariable("directory") Long directoryId,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type,
            @RequestPart(name = "file") MultipartFile file) throws IOException {
        ApiContextHolder.ApiContext context = ApiContextHolder.getContext();
        UserIdentity userIdentity = context.userInfo();

        if (userIdentity.getUserId() != ownerId) {
            throw new AuthenticationException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }

        LegalUserType legalUserType = LegalUserType.from(type);
        StorageOwner storageOwner = new SimpleStorageOwner(ownerId, legalUserType);
        FileStorageInfo fileStorageInfo = new FileStorageInfo(
                file.getOriginalFilename(), directoryId, storageOwner);
        String mimeType = file.getContentType();

        FileStreamInfo fileStreamInfo = new FileStreamInfo(
                file.getInputStream(),
                mimeType,
                FileType.fromMimeType(mimeType)
        );
        Storage storage = userFileStorageService.uploadFile(
                fileStorageInfo, fileStreamInfo);

        return HttpResponseEntity.success(StorageVo.from(storage, 0));
    }


    @GetMapping("/{type}/{ownerId}/disk/file/{fileId}/token")
    public void getDownloadFileUrl(@PathVariable("fileId") Long fileId,
                                   @PathVariable("ownerId") Long ownerId,
                                   @PathVariable("type") String type) {
        LegalUserType legalUserType = LegalUserType.from(type);
        AttributedStorage storage =
                userStorageSearchService.findFile(fileId);
    }

    // TODO: allow Content-Range and Content-Type in header
    @GetMapping("/{type}/{ownerId}/disk/file/{fileId}")
    public void downloadFile(@PathVariable("fileId") Long fileId,
                             @PathVariable("ownerId") Long ownerId,
                             @PathVariable("type") String type,
                             HttpServletResponse response) throws IOException {
        LegalUserType legalUserType = LegalUserType.from(type);
        userFileStorageService.downloadFile(
                fileId, new SimpleStorageOwner(ownerId, legalUserType),
                response.getOutputStream()
        );
    }

    @GetMapping("/{type}/{ownerId}/disk/{directory}/{fileName}")
    public void downloadFile(@PathVariable("directory") Long directory,
                             @PathVariable("ownerId") Long ownerId,
                             @PathVariable("type") String type,
                             @PathVariable("fileName") String fileName,
                             HttpServletResponse response) throws IOException {
        LegalUserType legalUserType = LegalUserType.from(type);

        userFileStorageService.downloadFile(
                new FileStorageInfo(fileName,
                        directory,
                        new SimpleStorageOwner(ownerId, legalUserType)
                ), response.getOutputStream()
        );
    }

    @BuiltinOperate(BuiltinOperationType.DELETE_FILE)
    @DeleteMapping("/{type}/{ownerId}/disk/{directory}/{fileName}")
    public HttpResponseEntity<Void> deleteFile(
            @PathVariable("directory") Long directory,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type,
            @PathVariable("fileName") String fileName) {
        ApiContextHolder.ApiContext context = ApiContextHolder.getContext();
        UserIdentity userIdentity = context.userInfo();
        if (userIdentity.getUserId() != ownerId) {
            throw new AuthenticationException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }

        LegalUserType legalUserType = LegalUserType.from(type);

        AttributedStorage storage = userStorageSearchService.findFile(
                new FileStorageInfo(fileName, directory, new SimpleStorageOwner(ownerId, legalUserType)));
        StorageAction storageAction =
                storageActionService.openStorageAction(storage);
        storageAction.delete();
        return HttpResponseEntity.success();
    }

    @GetMapping("/{type}/{ownerId}/disk/directory/{directory}")
    public HttpResponseEntity<List<StorageVo>> listFiles(
            @PathVariable("directory") Long directory,
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("type") String type) {
        List<AttributedStorage> storages = userStorageSearchService.listFiles(
                directory,
                new SimpleStorageOwner(ownerId, LegalUserType.from(type))
        );

        return HttpResponseEntity.success(
                storages.stream()
                        .map(StorageVo::from)
                        .toList()
        );
    }
}
