package org.huel.cloudhub.client.disk.domain.userstorage.event;

import org.huel.cloudhub.client.disk.domain.userstorage.UserFileStorage;
import org.springframework.context.ApplicationEvent;

/**
 * @author RollW
 */
public class OnFileCreateEvent extends ApplicationEvent {
    private final UserFileStorage userFileStorage;

    public OnFileCreateEvent(UserFileStorage userFileStorage) {
        super(userFileStorage);
        this.userFileStorage = userFileStorage;
    }

    public UserFileStorage getUserFileStorage() {
        return userFileStorage;
    }
}
