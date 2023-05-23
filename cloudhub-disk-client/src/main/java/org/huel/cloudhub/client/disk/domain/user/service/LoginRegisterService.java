package org.huel.cloudhub.client.disk.domain.user.service;

import com.google.common.base.Preconditions;
import org.huel.cloudhub.client.disk.domain.user.AttributedUser;
import org.huel.cloudhub.client.disk.domain.user.LoginLogService;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.dto.LoginLog;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfoSignature;
import org.huel.cloudhub.client.disk.domain.user.event.OnUserLoginEvent;
import org.huel.cloudhub.client.disk.domain.user.repository.UserRepository;
import org.huel.cloudhub.web.RequestMetadata;
import org.huel.cloudhub.web.Result;
import org.huel.cloudhub.web.UserErrorCode;
import org.huel.cloudhub.web.data.page.Offset;
import org.huel.cloudhub.web.data.page.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author RollW
 */
@Service
public class LoginRegisterService implements LoginLogService {
    private static final Logger logger = LoggerFactory.getLogger(LoginRegisterService.class);

    private final UserRepository userRepository;
    private final UserManageService userManageService;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserSignatureProvider userSignatureProvider;

    private final Deque<LoginLog> loginLogs =
            new ConcurrentLinkedDeque<>();

    public LoginRegisterService(UserRepository userRepository,
                                UserManageService userManageService,
                                ApplicationEventPublisher eventPublisher,
                                AuthenticationManager authenticationManager,
                                PasswordEncoder passwordEncoder,
                                UserSignatureProvider userSignatureProvider) {
        this.userRepository = userRepository;
        this.userManageService = userManageService;
        this.eventPublisher = eventPublisher;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userSignatureProvider = userSignatureProvider;
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
            logLogin(user, metadata, false);
            return Result.of(UserErrorCode.ERROR_PASSWORD_NOT_CORRECT);
        }
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        OnUserLoginEvent onUserLoginEvent = new OnUserLoginEvent(user);
        eventPublisher.publishEvent(onUserLoginEvent);

        logLogin(user, metadata, true);
        String signature = userSignatureProvider.getSignature(user);
        return Result.success(
                UserInfoSignature.from(user, signature)
        );
    }

    public AttributedUser registerUser(String username, String password,
                                       String email) {
        boolean hasUsers = userRepository.hasUsers();
        Role role = hasUsers ? Role.USER : Role.ADMIN;
        AttributedUser user =
                userManageService.createUser(username, password, email, role, true);
        logger.info("Register username: {}, email: {}, role: {}, id: {}",
                username, email,
                user.getRole(),
                user.getUserId()
        );
        return user;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public List<LoginLog> getLogs(Pageable pageable) {
        Offset offset = pageable.toOffset();

        return loginLogs.stream()
                .skip(offset.offset())
                .limit(offset.limit())
                .toList();
    }

    @Override
    public List<LoginLog> getUserLogs(long userId, Pageable pageable) {
        Offset offset = pageable.toOffset();
        return loginLogs.stream()
                .filter(loginLog -> loginLog.userId() == userId)
                .skip(offset.offset())
                .limit(offset.limit())
                .toList();
    }

    @Override
    public long getLogsCount() {
        return loginLogs.size();
    }

    @Override
    public long getUserLogsCount(long userId) {
        return loginLogs.stream()
                .filter(loginLog -> loginLog.userId() == userId)
                .count();
    }

    private void logLogin(User user, RequestMetadata metadata, boolean success) {
        LoginLog loginLog = LoginLog.from(user, metadata, success);
        if (loginLogs.size() >= 1000) {
            loginLogs.removeLast();
        }
        loginLogs.addFirst(loginLog);
    }
}
