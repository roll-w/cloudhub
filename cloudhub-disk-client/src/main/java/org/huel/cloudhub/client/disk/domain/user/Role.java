package org.huel.cloudhub.client.disk.domain.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * @author RollW
 */
public enum Role {
    USER(new SimpleGrantedAuthority("USER")),
    ADMIN(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("USER")),
    GUEST(new SimpleGrantedAuthority("GUEST"));

    private final List<GrantedAuthority> authority;

    Role(SimpleGrantedAuthority... authority) {
        this.authority = List.of(authority);
    }

    public boolean hasPrivilege() {
        return this == ADMIN;
    }

    public List<GrantedAuthority> toAuthority() {
        return authority;
    }

}
