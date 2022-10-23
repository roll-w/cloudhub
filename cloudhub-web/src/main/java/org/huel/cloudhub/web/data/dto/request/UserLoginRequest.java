package org.huel.cloudhub.web.data.dto.request;

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
