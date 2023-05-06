package org.huel.cloudhub.client.disk.domain.user;

import org.huel.cloudhub.client.disk.BaseAbility;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author RollW
 */
@BaseAbility
public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    UserDetails loadUserByUserId(long userId) throws UsernameNotFoundException;
}
