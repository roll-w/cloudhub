package org.huel.cloudhub.client.data.dto.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.huel.cloudhub.client.data.entity.user.Role;

/**
 * @author RollW
 */
public record UserCreateRequest(
        @NonNull
        String username,
        @NonNull
        String password,
        @Nullable
        String email,
        @Nullable
        Role role,
        @Nullable
        Boolean discardEmail) {
}
