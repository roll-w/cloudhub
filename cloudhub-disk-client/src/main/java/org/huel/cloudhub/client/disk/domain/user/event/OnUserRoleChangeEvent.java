package org.huel.cloudhub.client.disk.domain.user.event;

import org.huel.cloudhub.client.disk.domain.user.Role;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.springframework.context.ApplicationEvent;
import space.lingu.NonNull;
import space.lingu.Nullable;

/**
 * @author RollW
 */
public class OnUserRoleChangeEvent extends ApplicationEvent {
    private final UserInfo userInfo;
    @Nullable
    private final Role previousRole;
    @NonNull
    private final Role currentRole;

    public OnUserRoleChangeEvent(UserInfo userInfo,
                                 @Nullable Role previousRole,
                                 @NonNull Role currentRole) {
        super(userInfo);
        this.userInfo = userInfo;
        this.previousRole = previousRole;
        this.currentRole = currentRole;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Nullable
    public Role getPreviousRole() {
        return previousRole;
    }

    @NonNull
    public Role getCurrentRole() {
        return currentRole;
    }
}
