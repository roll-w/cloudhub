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
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        var res = bucketService.createBucket(
                userInfo.id(),
                bucketCreateRequest.bucketName(),
                bucketCreateRequest.visibility());
        return HttpResponseEntity.create(res.toResponseBody());
    }


    @PostMapping("/delete")
    //@DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(HttpServletRequest request, @RequestParam Map<String, String> map) {
        String bucketName = map.get("bucketName");
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        var res =
                bucketService.deleteBucket(userInfo.id(), bucketName);
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @GetMapping("/get")
    public HttpResponseEntity<BucketInfo> getBucket(@RequestParam String bucketName) {
        Bucket bucket = bucketService.getBucketByName(bucketName);
        if (bucket == null) {
            return HttpResponseEntity.failure("Bucket not exist.",
                    ErrorCode.ERROR_DATA_NOT_EXIST);
        }
        return HttpResponseEntity.success(bucket.toInfo());
    }


    @GetMapping("/get/all")
    public HttpResponseEntity<List<BucketInfo>> getBuckets(HttpServletRequest request) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        return HttpResponseEntity.success(
                bucketService.getUserBuckets(userInfo.id()));
    }
}
