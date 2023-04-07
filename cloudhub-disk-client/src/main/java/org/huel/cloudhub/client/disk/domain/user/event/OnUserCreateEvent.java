package org.huel.cloudhub.client.disk.domain.user.event;

import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.springframework.context.ApplicationEvent;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public class OnUserCreateEvent extends ApplicationEvent {
    private final UserInfo userInfo;

    public OnUserCreateEvent(@NonNull UserInfo userInfo) {
        super(userInfo);
        this.userInfo = userInfo;
    }

    @NonNull
    public final UserInfo getUserInfo() {
        return userInfo;
    }
}
