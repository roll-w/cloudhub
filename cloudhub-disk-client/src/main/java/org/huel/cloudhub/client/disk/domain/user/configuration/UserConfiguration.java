package org.huel.cloudhub.client.disk.domain.user.configuration;

import org.huel.cloudhub.client.disk.domain.user.filter.UserInfoFilter;
import org.huel.cloudhub.client.disk.domain.user.filter.UserInfoFilterChain;
import org.huel.cloudhub.client.disk.domain.user.filter.UserInfoFormatValidateFilter;
import org.huel.cloudhub.client.disk.domain.user.filter.UserInfoSensitiveWordFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RollW
 */
@Configuration
public class UserConfiguration {

    @Bean
    public UserInfoFilter userInfoFilter() {
        return UserInfoFilterChain.connect(
                new UserInfoFormatValidateFilter(),
                new UserInfoSensitiveWordFilter()
        );
    }
}
