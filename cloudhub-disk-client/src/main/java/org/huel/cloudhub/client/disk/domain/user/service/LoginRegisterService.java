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

package org.huel.cloudhub.client.disk.domain.user.service;

import com.google.cloud.audit.RequestMetadata;
import com.google.common.base.Preconditions;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfoSignature;
import org.huel.cloudhub.client.disk.domain.user.event.OnUserLoginEvent;
import org.huel.cloudhub.client.disk.domain.user.event.OnUserRegistrationEvent;
import org.huel.cloudhub.client.disk.domain.user.repository.UserRepository;
import org.huel.cloudhub.common.Result;
import org.huel.cloudhub.web.UserErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * @author RollW
 */
@Service
public class LoginRegisterService  {
    private static final Logger logger = LoggerFactory.getLogger(LoginRegisterService.class);

    private final UserRepository userRepository;
    private final UserManageService userManageService;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public LoginRegisterService(UserRepository userRepository,
                                UserManageService userManageService,
                                ApplicationEventPublisher eventPublisher,
                                AuthenticationManager authenticationManager,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userManageService = userManageService;
        this.eventPublisher = eventPublisher;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }


    private User tryGetUser(String identity) {
        if (identity.contains("@")) {
            return userRepository.getUserByEmail(identity);
        }
        return userRepository.getUserByName(identity);
    }

    public Result<UserInfoSignature> loginUser(String identity,
                                               String token,
                                               RequestMetadata metadata) {
        Preconditions.checkNotNull(identity, "identity cannot be null");
        Preconditions.checkNotNull(token, "token cannot be null");

        User user = tryGetUser(identity);
        if (user == null) {
            return Result.of(UserErrorCode.ERROR_USER_NOT_EXIST);
        }
        if (!passwordEncoder.matches(token, user.getPassword())) {
            return Result.of(UserErrorCode.ERROR_PASSWORD_NOT_CORRECT);
        }
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        OnUserLoginEvent onUserLoginEvent = new OnUserLoginEvent(user);
        eventPublisher.publishEvent(onUserLoginEvent);

        return Result.success(
                UserInfoSignature.from(user)
        );
    }

    public Result<UserInfo> registerUser(String username, String password,
                                         String email) {
        boolean hasUsers = userRepository.hasUsers();
        Role role = hasUsers ? Role.USER : Role.ADMIN;
        boolean enabled = !hasUsers;
        Result<UserInfo> userInfoResult =
                userManageService.createUser(username, password, email, role, enabled);
        if (userInfoResult.failed()) {
            return userInfoResult;
        }

        if (!enabled) {
            OnUserRegistrationEvent event = new OnUserRegistrationEvent(
                    userInfoResult.data(), Locale.getDefault(),
                    "http://localhost:5000/user/register/activate/");
            // TODO: get url from config
            eventPublisher.publishEvent(event);
        }

        logger.info("Register username: {}, email: {}, role: {}, id: {}",
                username, email,
                userInfoResult.data().getRole(),
                userInfoResult.data().getUserId()
        );
        return userInfoResult;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
