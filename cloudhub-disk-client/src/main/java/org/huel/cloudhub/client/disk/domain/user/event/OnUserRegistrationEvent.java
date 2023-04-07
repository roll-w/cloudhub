package org.huel.cloudhub.client.disk.domain.user.event;

import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

/**
 * @author RollW
 */
public class OnUserRegistrationEvent extends ApplicationEvent {
    private final UserInfo userInfo;
    /**
     * Confirm url prefix.
     */

    private final String url;
    private final Locale locale;

    public OnUserRegistrationEvent(UserInfo userInfo,
                                   Locale locale,
                                   String url) {
        super(userInfo);
        this.userInfo = userInfo;
        this.locale = locale;
        this.url = url;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getUrl() {
        return url;
    }

    public Locale getLocale() {
        return locale;
    }
}
