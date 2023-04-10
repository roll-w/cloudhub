package org.huel.cloudhub.client.disk.domain.user.event;

import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.springframework.context.ApplicationEvent;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class OnUserLoginEvent extends ApplicationEvent {
    @NonNull
    private final UserIdentity userInfo;


    public OnUserLoginEvent(@NonNull UserIdentity userInfo) {
        super(userInfo);
        this.userInfo = userInfo;
    }

    @NonNull
    public UserIdentity getUserInfo() {
        return userInfo;
    }
}
