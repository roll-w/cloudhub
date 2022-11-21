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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Cheng
 */

@RestController
@BucketAdminApi
public class BucketManagerController {
    private final BucketService bucketService;
    private final UserGetter userGetter;

    public BucketManagerController(BucketService bucketService, UserGetter userGetter) {
        this.bucketService = bucketService;
        this.userGetter = userGetter;
    }


    @PutMapping("/create")
    public HttpResponseEntity<BucketInfo> create(HttpServletRequest request,
                                                 @RequestBody BucketAdminCreateRequest bucketCreateRequest) {
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<BucketInfo>) httpResponse;
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


    @PostMapping("/delete")
    //@DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(HttpServletRequest request,
                                           @RequestBody BucketAdminDeleteRequest bucketAdminDeleteRequest) {
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<Void>) httpResponse;
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
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<BucketInfo>) httpResponse;
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

        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<List<BucketInfo>>) httpResponse;
        }
        if (userId == null) {
            return HttpResponseEntity.success(bucketService.getAllUsersBuckets());
        }
        return HttpResponseEntity.success(bucketService.getUserBuckets(userId));
    }


    private HttpResponseEntity<?> validate(HttpServletRequest request) {
        return ValidateHelper.getHttpResponseEntity(request, userGetter);
    }



}
