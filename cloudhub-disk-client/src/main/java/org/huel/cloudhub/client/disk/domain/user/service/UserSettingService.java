/*
 * Copyright (C) 2023 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
