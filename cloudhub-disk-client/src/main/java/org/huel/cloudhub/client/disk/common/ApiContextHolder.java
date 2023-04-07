package org.huel.cloudhub.client.disk.common;

import com.google.common.base.Preconditions;
import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.springframework.http.HttpMethod;
import space.lingu.Nullable;

import java.util.Locale;

/**
 * @author RollW
 */
public final class ApiContextHolder {
    private static final ThreadLocal<ApiContext> contextHolder = new ThreadLocal<>();

    public static void setContext(ApiContext context) {
        Preconditions.checkNotNull(context, "context cannot be null");
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static ApiContext getContext() {
        return contextHolder.get();
    }

    public static boolean hasContext() {
        return contextHolder.get() != null;
    }

    private ApiContextHolder() {
    }

    public record ApiContext(
            boolean admin,
            String ip,
            Locale locale,
            HttpMethod method,
            @Nullable
            UserInfo userInfo
    ) {
        public long id() {
            Preconditions.checkNotNull(userInfo, "userInfo cannot be null");
            return userInfo.id();
        }

        @Nullable
        public Long rawId() {
            if (userInfo == null) {
                return null;
            }
            return userInfo.id();
        }

        public String username() {
            Preconditions.checkNotNull(userInfo, "userInfo cannot be null");
            return userInfo.username();
        }

        public String email() {
            Preconditions.checkNotNull(userInfo, "userInfo cannot be null");
            return userInfo.email();
        }

        public Role role() {
            Preconditions.checkNotNull(userInfo, "userInfo cannot be null");
            return userInfo.role();
        }

        public boolean hasPrivilege() {
            return role().hasPrivilege();
        }

        public boolean equalsUserId(Long userId) {
            if (userId == null) {
                return false;
            }
            if (userInfo == null) {
                return false;
            }
            return userInfo.id() == userId;
        }

        /**
         * Allow access to resource, only if the user is admin and the access API is
         * the admin API or the resource is owned by the user.
         */
        public boolean allowAccessResource(Long userId) {
            return isAdminPass() || equalsUserId(userId);
        }

        /**
         * Check if the user is admin and the access API is admin api.
         */
        public boolean isAdminPass() {
            return admin && hasPrivilege();
        }
    }

}
