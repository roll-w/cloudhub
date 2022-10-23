package org.huel.cloudhub.web.event.user.login;

import org.huel.cloudhub.web.data.dto.UserInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author RollW
 */
public class OnLoginNewLocationEvent extends ApplicationEvent {
    private final UserInfo userInfo;

    // private DeviceMetadata deviceMetadata;
    // TODO

    public OnLoginNewLocationEvent(UserInfo info) {
        super(info);
        this.userInfo = info;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
