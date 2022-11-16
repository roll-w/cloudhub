package org.huel.cloudhub.client.data.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author RollW
 */
public record UserLoginRequest(
        @JsonProperty("username")
        String username,

        @JsonProperty("password")
        String password) {
}
