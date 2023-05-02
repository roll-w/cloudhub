package org.huel.cloudhub.client.disk.domain.user.dto;

import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author RollW
 */
public record UserInfoSignature(
        long id,
        String username,
        String signature,
        String email,
        Role role
) implements UserIdentity {
    public static UserInfoSignature from(User user) {
        if (user == null) {
            return null;
        }
        return new UserInfoSignature(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole()
        );
    }

    public static UserInfoSignature from(UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            return null;
        }
        return new UserInfoSignature(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole()
        );
    }

    public UserInfo toUserInfo() {
        return new UserInfo(
                id,
                username,
                email,
                role
        );
    }

    @Override
    public long getUserId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Role getRole() {
        return role;
    }
}
