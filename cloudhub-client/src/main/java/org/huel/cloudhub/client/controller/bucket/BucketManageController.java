package org.huel.cloudhub.client.controller.bucket;

import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.data.dto.bucket.BucketAdminCreateRequest;
import org.huel.cloudhub.client.data.dto.bucket.BucketAdminDeleteRequest;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.service.bucket.BucketService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.web.bind.annotation.*;

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
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody((data) -> null));
        }
        // 有警告应该问题不大不太懂
        if (bucketCreateRequest.userId() == null) {
            return HttpResponseEntity.failure("No given user id.",
                    ErrorCode.ERROR_PARAM_MISSING);
        }
        var res = bucketService.createBucket(
                bucketCreateRequest.userId(),
                bucketCreateRequest.bucketName(),
                bucketCreateRequest.visibility());
        return HttpResponseEntity.create(res.toResponseBody());
    }


    @DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(HttpServletRequest request,
                                           @RequestBody BucketAdminDeleteRequest bucketAdminDeleteRequest) {
        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody((data) -> null));
        }
        if (bucketAdminDeleteRequest.userId() == null) {
            return HttpResponseEntity.failure("No given user id.",
                    ErrorCode.ERROR_PARAM_MISSING);
        }
        if (bucketAdminDeleteRequest.bucketName() == null) {
            return HttpResponseEntity.failure("Bucket name missing",
                    ErrorCode.ERROR_PARAM_MISSING);
        }
        var res = bucketService.deleteBucket(
                bucketAdminDeleteRequest.userId(),
                bucketAdminDeleteRequest.bucketName());
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @GetMapping("/get")
    public HttpResponseEntity<BucketInfo> getBucket(HttpServletRequest request,
                                                    @RequestParam String bucketName) {
        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody((data) -> null));
        }
        Bucket bucket = bucketService.getBucketByName(bucketName);
        if (bucket == null) {
            return HttpResponseEntity.failure("Bucket not exist.",
                    ErrorCode.ERROR_DATA_NOT_EXIST);
        }
        return HttpResponseEntity.success(bucket.toInfo());
    }


    @GetMapping("/get/all")
    public HttpResponseEntity<List<BucketInfo>> getBuckets(HttpServletRequest request,
                                                           @RequestParam(required = false) Long userId) {

        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
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
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody((data) -> null));
        }
        return HttpResponseEntity.success(bucketService.getBucketsCount());
    }

    @PostMapping("/setting/visibility")
    public HttpResponseEntity<BucketInfo> changeVisibility(
            HttpServletRequest request, @RequestBody BucketAdminCreateRequest createRequest) {
        var validateMessage = validate(request);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody((data) -> null));
        }
        var res = bucketService.setVisibility(
                createRequest.bucketName(),
                createRequest.visibility());
        return HttpResponseEntity.create(res.toResponseBody());
    }


    private MessagePackage<?> validate(HttpServletRequest request) {
        return ValidateHelper.validateUserAdmin(request, userGetter);
    }
}
