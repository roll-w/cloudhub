package org.huel.cloudhub.client.disk.domain.user.service;

/**
 * @author RollW
 */
public interface UserSettingService {
    void changePassword(long userId, String newPassword);

    void changeEmail(long userId, String newEmail);

    void changeUsername(long userId, String newUsername);

    void changeAvatar(long userId, String newAvatar);

    void changeGender(long userId, String newGender);

    void changeBirthday(long userId, String newBirthday);

    void changeLocation(long userId, String newLocation);

    void changeWebsite(long userId, String newWebsite);

    void changeBio(long userId, String newBio);
}
