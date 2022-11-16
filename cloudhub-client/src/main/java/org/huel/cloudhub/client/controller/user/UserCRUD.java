package org.huel.cloudhub.client.controller.user;

import org.huel.cloudhub.client.data.dto.UserInfo;
import org.huel.cloudhub.client.data.entity.user.Role;
import org.huel.cloudhub.client.service.user.UserManageService;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.MessagePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Author Cheng
 *
 */

@RestController
@RequestMapping("/user")
public class UserCRUD {

    @Autowired
    private UserManageService userManageService;

//    随意选择的构造器，返回值不太确定
    @PostMapping("/insertOrUpdate")
    public  MessagePackage<UserInfo> create(@RequestParam Map<String,String> map){
        userManageService.createUser(
                map.get("username"),
                map.get("password"),
                map.get("email"),
                Role.USER,false);
        return new MessagePackage<UserInfo>(ErrorCode.SUCCESS,null);
    }

    @GetMapping("/delete")
    public  MessagePackage<UserInfo> delete(@RequestParam Map<String,String> map){
        userManageService.deleteUser(Long.parseLong(map.get("userId")));
        return new MessagePackage<UserInfo>(ErrorCode.SUCCESS,null);
    }

    @GetMapping("/query")
    public MessagePackage<UserInfo> query(@RequestParam String username){
        userManageService.queryUser(username);
        return new MessagePackage<UserInfo>(ErrorCode.SUCCESS,null);
    }
}
