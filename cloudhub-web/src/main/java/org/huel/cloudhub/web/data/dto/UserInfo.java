package org.huel.cloudhub.web.data.dto;

import org.huel.cloudhub.web.data.entity.user.User;
import space.lingu.light.DataColumn;

/**
 * Fragment of {@link User}
 *
 * @author RollW
 */
public record UserInfo(
        @DataColumn(name = "user_id")
        Long id,

        @DataColumn(name = "user_name")
        String username,

        @DataColumn(name = "user_email")
        String email
) {
}
