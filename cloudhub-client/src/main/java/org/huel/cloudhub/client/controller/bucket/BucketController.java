package org.huel.cloudhub.client.controller.bucket;

import org.huel.cloudhub.client.data.dto.bucket.BucketCreateRequest;
import org.huel.cloudhub.client.data.dto.bucket.BucketInfo;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.data.entity.bucket.Bucket;
import org.huel.cloudhub.client.data.entity.bucket.BucketVisibility;
import org.huel.cloudhub.client.data.entity.user.User;
import org.huel.cloudhub.client.service.bucket.BucketService;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Cheng
 */

@RestController
public class BucketController {
    private final BucketService bucketService;

    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @PutMapping("/create")
    public HttpResponseEntity<BucketInfo> create(@RequestParam Map<String, String> map){
//        service自动校验参数不用校验了吧，BucketCreateRequest不会用待修改
        //参考UserManagerController貌似没有调用sercice层的create，？？？
        long userId = Long.parseLong(map.get("userId"));
        String bucketName = map.get("bucketName");
        BucketVisibility bucketVisibility = BucketVisibility.valueOf(map.get("bucketVisibility"));
        bucketService.createBucket(userId,bucketName,bucketVisibility);
        return HttpResponseEntity.success();
    }


    @PostMapping("/delete")
    //@DeleteMapping("/delete")
    public HttpResponseEntity<Void> delete(@RequestParam Map<String, String> map) {
        var res =
                bucketService.deleteBucketByName(map.get("userId"));
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @GetMapping("/get")
    public HttpResponseEntity<BucketInfo> getUser(@RequestParam String bucketName) {
        Bucket bucket = bucketService.queryByName(bucketName);
        if (bucket == null) {
            return HttpResponseEntity.failure("bucket not exist",
                    ErrorCode.ERROR_NULL,  null);
        }
        return HttpResponseEntity.success(bucket.toInfo());
    }
}
