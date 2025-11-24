package io.github.prjkmo112.mainapi.security.user;

import io.github.prjkmo112.commonmysqldb.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class UserBean implements UserDetails {

    private final Long userId;
    private final String username;
    private final String email;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public UserBean(User user) {
        this.userId = user.getId();
        this.username = user.getName();
        this.email = user.getEmail();
        this.password = user.getPasswd();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public String toString() {
        return username;
    }
}
