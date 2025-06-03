package com.wizard.core.security;

import com.wizard.core.entity.User;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String username;
    private final String password;
    private final String role;
    private final boolean activo;

    public UserPrincipal(UUID id, String username, String password, String role, boolean activo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.activo = activo;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActivo());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role); // simple ROLE support
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return activo; }
}
