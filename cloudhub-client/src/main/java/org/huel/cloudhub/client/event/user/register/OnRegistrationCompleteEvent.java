package org.huel.cloudhub.client.event.user.register;

import org.huel.cloudhub.client.data.dto.UserInfo;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

/**
 * @author RollW
 */
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    /**
     * 应用Url
     */
    private final String url;
    private final Locale locale;
    private final UserInfo user;

    public OnRegistrationCompleteEvent(UserInfo user,
                                       Locale locale,
                                       String url) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Locale getLocale() {
        return locale;
    }

    public UserInfo getUser() {
        return user;
    }
}
