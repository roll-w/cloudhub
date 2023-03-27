/*
 * Copyright (C) 2023 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huel.cloudhub.client.disk.controller.user;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.service.UserManageService;
import org.huel.cloudhub.client.disk.domain.user.vo.UserCommonDetailsVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class UserManageController {
    private final UserManageService userManageService;

    public UserManageController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    @GetMapping("/users")
    public HttpResponseEntity<List<UserCommonDetailsVo>> getUserList(Pageable pageRequest) {
        List<User> userIdentities = userManageService.getUsers(
                pageRequest.getPage(),
                pageRequest.getSize()
        );
        return HttpResponseEntity.success(
                userIdentities.stream().map(
                        UserCommonDetailsVo::of
                ).toList()
        );
    }

    @GetMapping("/user/{userId}")
    public HttpResponseEntity<UserCommonDetailsVo> getUserDetails(
            @PathVariable Long userId) {
        User user = userManageService.getUser(userId);
        UserCommonDetailsVo userDetailsVo =
                UserCommonDetailsVo.of(user);
        return HttpResponseEntity.success(userDetailsVo);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@PathVariable String userId) {

    }

    @PutMapping("/user/{userId}")
    public void updateUser(@PathVariable String userId) {

    }

    @PostMapping("/user")
    public void createUser() {

    }

    @PutMapping("/user/{userId}/blocks")
    public void blockUser(@PathVariable String userId) {

    }

    @DeleteMapping("/user/{userId}/blocks")
    public void unblockUser(@PathVariable String userId) {

    }

    @GetMapping("/user/{userId}/blocks")
    public void getBlockedUserList(@PathVariable String userId) {

    }
}
