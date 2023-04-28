package org.huel.cloudhub.client.disk.domain.user.dto;

import org.huel.cloudhub.client.disk.domain.user.LegalUserType;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.springframework.security.core.userdetails.UserDetails;
import space.lingu.light.DataColumn;

/**
 * @author RollW
 */
public record UserInfo(
        @DataColumn(name = "id")
        long id,

        @DataColumn(name = "username")
        String username,

        @DataColumn(name = "email")
        String email,

        @DataColumn(name = "role")
        Role role
) implements UserIdentity, StorageOwner {
    public static UserInfo from(User user) {
        if (user == null) {
            return null;
        }
        return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    public static UserInfo from(UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            return null;
        }
        return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
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

    @Override
    public long getOwnerId() {
        return id;
    }

    @Override
    public LegalUserType getOwnerType() {
        return LegalUserType.USER;
    }
}
