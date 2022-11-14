package org.huel.cloudhub.client.event.user;

import org.huel.cloudhub.client.data.dto.UserInfo;
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
