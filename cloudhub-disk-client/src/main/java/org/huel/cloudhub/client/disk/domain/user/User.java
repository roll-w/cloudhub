package org.huel.cloudhub.client.disk.domain.user;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import space.lingu.light.Constructor;
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.LightConfiguration;
import space.lingu.light.PrimaryKey;

import java.util.Collection;
import java.util.Objects;

/**
 * @author RollW
 */
@DataTable(name = "user", indices = {
        @Index(value = "username", unique = true)
})
@LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "120")
@SuppressWarnings({"ClassCanBeRecord"})
public class User implements UserDetails, AttributedUser, StorageOwner, DataItem {
    @PrimaryKey(autoGenerate = true)
    @DataColumn(name = "id")
    private final Long id;

    @DataColumn(name = "username", nullable = false)
    private final String username;

    @DataColumn(name = "nickname")
    private final String nickname;

    @DataColumn(name = "password", nullable = false)
    private final String password;

    @DataColumn(name = "role")
    @LightConfiguration(key = LightConfiguration.KEY_VARCHAR_LENGTH, value = "20")
    private final Role role;

    @DataColumn(name = "register_time")
    private final long registerTime;

    @DataColumn(name = "update_time")
    private final long updateTime;

    @DataColumn(name = "email")
    private final String email;

    @DataColumn(name = "enabled")
    private final boolean enabled;

    /**
     *
     */
    @DataColumn(name = "locked")
    private final boolean locked;

    /**
     *
     */
    @DataColumn(name = "account_expired")
    private final boolean accountExpired;

    /**
     * 账号注销
     */
    @DataColumn(name = "account_canceled")
    private final boolean canceled;

    @Constructor
    public User(Long id, String username, String nickname,
                String password,
                Role role, long registerTime,
                long updateTime, String email,
                boolean enabled, boolean locked,
                boolean accountExpired,
                boolean canceled) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.registerTime = registerTime;
        this.updateTime = updateTime;
        this.email = email;
        this.enabled = enabled;
        this.locked = locked;
        this.accountExpired = accountExpired;
        this.canceled = canceled;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public long getUserId() {
        return getId();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public long getCreateTime() {
        return getRegisterTime();
    }

    @Override
    public long getUpdateTime() {
        return updateTime;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired || !canceled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.toAuthority();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .setId(id)
                .setUsername(username)
                .setNickname(nickname)
                .setPassword(password)
                .setRole(role)
                .setRegisterTime(registerTime)
                .setUpdateTime(updateTime)
                .setEmail(email)
                .setEnabled(enabled)
                .setLocked(locked)
                .setAccountExpired(accountExpired)
                .setCanceled(canceled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return registerTime == user.registerTime && enabled == user.enabled && locked == user.locked && accountExpired == user.accountExpired && canceled == user.canceled && Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && role == user.role && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role, registerTime, email, enabled, locked, accountExpired, canceled);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", registerTime=" + registerTime +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", locked=" + locked +
                ", accountExpired=" + accountExpired +
                ", canceled=" + canceled +
                '}';
    }

    public static boolean isInvalidId(Long userId) {
        if (userId == null) {
            return true;
        }
        return userId <= 0;
    }

    public static final class Builder {
        private Long id = null;
        private String username;
        private String nickname;
        private String password;
        private Role role = Role.USER;
        private long registerTime;
        private long updateTime;
        private String email;
        private boolean enabled;
        private boolean locked = false;
        private boolean accountExpired = false;
        private boolean canceled = false;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder setRegisterTime(long registerTime) {
            this.registerTime = registerTime;
            return this;
        }

        public Builder setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setLocked(boolean locked) {
            this.locked = locked;
            return this;
        }

        public Builder setAccountExpired(boolean accountExpired) {
            this.accountExpired = accountExpired;
            return this;
        }

        public Builder setCanceled(boolean canceled) {
            this.canceled = canceled;
            return this;
        }

        public User build() {
            if (nickname == null) {
                nickname = username;
            }

            return new User(
                    id, username, nickname, password,
                    role, registerTime,
                    updateTime, email, enabled, locked,
                    accountExpired, canceled);
        }
    }
}
