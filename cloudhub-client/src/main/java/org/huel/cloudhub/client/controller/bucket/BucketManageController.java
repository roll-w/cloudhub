package org.huel.cloudhub.client.controller.bucket;

import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.data.dto.bucket.BucketAdminCreateRequest;
import org.huel.cloudhub.client.data.dto.bucket.BucketAdminDeleteRequest;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.service.bucket.BucketErrorCode;
import org.huel.cloudhub.client.service.bucket.BucketRuntimeException;
import org.huel.cloudhub.client.service.bucket.BucketService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.BusinessRuntimeException;
import org.huel.cloudhub.common.MessagePackage;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.WebCommonErrorCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Cheng
 */

@RestController
@BucketAdminApi
public class BucketManageController {
    private final BucketService bucketService;
    private final UserGetter userGetter;

    public BucketManageController(BucketService bucketService, UserGetter userGetter) {
        this.bucketService = bucketService;
        this.userGetter = userGetter;
    }


    @PutMapping("/create")
    public HttpResponseEntity<BucketInfo> create(HttpServletRequest request,
                                                 @RequestBody BucketAdminCreateRequest bucketCreateRequest) {
        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.of(
                    validateMessage.toResponseBody((data) -> null));
        }
        // 有警告应该问题不大不太懂
        if (bucketCreateRequest.userId() == null) {
            throw new BusinessRuntimeException(
                    WebCommonErrorCode.ERROR_PARAM_MISSING, "No user id");
        }
        var res = bucketService.createBucket(
                bucketCreateRequest.userId(),
                bucketCreateRequest.bucketName(),
                bucketCreateRequest.visibility());
        return HttpResponseEntity.success(res);
    }


    @DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(HttpServletRequest request,
                                           @RequestBody BucketAdminDeleteRequest bucketAdminDeleteRequest) {
        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.of(
                    validateMessage.toResponseBody((data) -> null));
        }
        if (bucketAdminDeleteRequest.userId() == null) {
            throw new BusinessRuntimeException(
                    WebCommonErrorCode.ERROR_PARAM_MISSING, "No user id");
        }
        if (bucketAdminDeleteRequest.bucketName() == null) {
            throw new BusinessRuntimeException(
                    WebCommonErrorCode.ERROR_PARAM_MISSING, "No bucket name");
        }
        bucketService.deleteBucket(
                bucketAdminDeleteRequest.userId(),
                bucketAdminDeleteRequest.bucketName());
        return HttpResponseEntity.success();
    }

    @GetMapping("/get")
    public HttpResponseEntity<BucketInfo> getBucket(HttpServletRequest request,
                                                    @RequestParam String bucketName) {
        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.of(
                    validateMessage.toResponseBody((data) -> null));
        }
        Bucket bucket = bucketService.getBucketByName(bucketName);
        if (bucket == null) {
            throw new BucketRuntimeException(BucketErrorCode.ERROR_BUCKET_NOT_EXIST);
        }
        return HttpResponseEntity.success(bucket.toInfo());
    }


    @GetMapping("/get/all")
    public HttpResponseEntity<List<BucketInfo>> getBuckets(HttpServletRequest request,
                                                           @RequestParam(required = false) Long userId) {

        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.of(
                    validateMessage.toResponseBody((data) -> null));
        }
        if (userId == null) {
            return HttpResponseEntity.success(bucketService.getAllUsersBuckets());
        }
        return HttpResponseEntity.success(bucketService.getUserBuckets(userId));
    }

    @GetMapping("/get/size")
    public HttpResponseEntity<Integer> getBucketSize(HttpServletRequest request) {

        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.of(
                    validateMessage.toResponseBody((data) -> null));
        }
        return HttpResponseEntity.success(bucketService.getBucketsCount());
    }

    @PostMapping("/setting/visibility")
    public HttpResponseEntity<BucketInfo> changeVisibility(
            HttpServletRequest request, @RequestBody BucketAdminCreateRequest createRequest) {
        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.of(
                    validateMessage.toResponseBody((data) -> null));
        }
        var res = bucketService.setVisibility(
                createRequest.bucketName(),
                createRequest.visibility());
        return HttpResponseEntity.success(res);
    }


    private MessagePackage<?> validate(HttpServletRequest request) {
        return ValidateHelper.validateUserAdmin(request, userGetter);
    }
}
