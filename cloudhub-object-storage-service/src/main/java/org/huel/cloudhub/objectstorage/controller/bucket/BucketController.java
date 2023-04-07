package org.huel.cloudhub.objectstorage.controller.bucket;

import org.huel.cloudhub.objectstorage.data.dto.bucket.BucketCreateRequest;
import org.huel.cloudhub.objectstorage.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.huel.cloudhub.objectstorage.data.entity.bucket.Bucket;
import org.huel.cloudhub.objectstorage.service.bucket.BucketErrorCode;
import org.huel.cloudhub.objectstorage.service.bucket.BucketRuntimeException;
import org.huel.cloudhub.objectstorage.service.bucket.BucketService;
import org.huel.cloudhub.objectstorage.service.user.UserGetter;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.UserErrorCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 提供给用户的Bucket接口
 *
 * @author Cheng
 */
@RestController
@BucketApi
public class BucketController {
    private final BucketService bucketService;
    private final UserGetter userGetter;

    public BucketController(BucketService bucketService,
                            UserGetter userGetter) {
        this.bucketService = bucketService;
        this.userGetter = userGetter;
    }

    @PutMapping("/create")
    public HttpResponseEntity<BucketInfo> create(HttpServletRequest request,
                                                 @RequestBody BucketCreateRequest bucketCreateRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            throw new BusinessRuntimeException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }
        BucketInfo bucketInfo = bucketService.createBucket(
                userInfo.id(),
                bucketCreateRequest.bucketName(),
                bucketCreateRequest.visibility());
        return HttpResponseEntity.success(bucketInfo);
    }


    @DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(HttpServletRequest request,
                                           @RequestBody Map<String, String> map) {
        String bucketName = map.get("bucketName");
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            throw new BusinessRuntimeException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }
        bucketService.deleteBucket(userInfo.id(), bucketName);
        return HttpResponseEntity.success();
    }

    @GetMapping("/get")
    public HttpResponseEntity<BucketInfo> getBucket(@RequestParam String bucketName) {
        Bucket bucket = bucketService.getBucketByName(bucketName);
        if (bucket == null) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EXIST);
        }
        return HttpResponseEntity.success(bucket.toInfo());
    }


    @GetMapping("/get/all")
    public HttpResponseEntity<List<BucketInfo>> getBuckets(HttpServletRequest request) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            throw new BucketRuntimeException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }
        return HttpResponseEntity.success(
                bucketService.getUserBuckets(userInfo.id()));
    }

    @PostMapping("/setting/visibility")
    public HttpResponseEntity<BucketInfo> changeVisibility(
            HttpServletRequest request,
            @RequestBody BucketCreateRequest bucketCreateRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            throw new BusinessRuntimeException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }
        Bucket bucket = bucketService.getBucketByName(bucketCreateRequest.bucketName());
        if (bucket == null) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EXIST);

        }
        if (!Objects.equals(bucket.getUserId(), userInfo.id())) {
            throw new BucketRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
        BucketInfo bucketInfo = bucketService.setVisibility(
                bucketCreateRequest.bucketName(),
                bucketCreateRequest.visibility());
        return HttpResponseEntity.success(bucketInfo);
    }
}
