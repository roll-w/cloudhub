package org.huel.cloudhub.client.controller.bucket;

import org.huel.cloudhub.client.data.dto.bucket.BucketCreateRequest;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.service.bucket.BucketService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Cheng
 */

@RestController
public class BucketManagerController {
    private final BucketService bucketService;
    private final UserGetter userGetter;

    public BucketManagerController(BucketService bucketService, UserGetter userGetter) {
        this.bucketService = bucketService;
        this.userGetter = userGetter;
    }


    @PutMapping("/create")
    public HttpResponseEntity<BucketInfo> create(HttpServletRequest request,
                                                 @RequestBody BucketCreateRequest bucketCreateRequest) {
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<BucketInfo>) httpResponse;
        }
//        有警告应该问题不大不太懂
        UserInfo userInfo = userGetter.getCurrentUser(request);
        var res = bucketService.createBucket(
                userInfo.id(),
                bucketCreateRequest.bucketName(),
                bucketCreateRequest.visibility());
        return HttpResponseEntity.create(res.toResponseBody());
    }


    @PostMapping("/delete")
    //@DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(HttpServletRequest request, @RequestParam Map<String, String> map) {
        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<Void>) httpResponse;
        }
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        String bucketName = map.get("bucketName");
        var res =
                bucketService.deleteBucket(userInfo.id(), bucketName);
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @GetMapping("/get")
    public HttpResponseEntity<BucketInfo> getBucket(HttpServletRequest request,@RequestParam String bucketName) {
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
    public HttpResponseEntity<List<BucketInfo>> getBuckets(HttpServletRequest request) {

        var httpResponse = validate(request);
        if (httpResponse != null) {
            return (HttpResponseEntity<List<BucketInfo>>) httpResponse;
        }
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        return HttpResponseEntity.success(
                bucketService.getUserBuckets(userInfo.id()));
    }

    private HttpResponseEntity<?> validate(HttpServletRequest request) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        if (!userInfo.role().hasPrivilege()) {
            return HttpResponseEntity.failure("User has no permissions.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        return null;
    }

}
