package com.example.domain;

import com.example.model.Role;
import com.example.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

public class UserDetailAuth implements UserDetails {

    private final String email;
    private final String password;
    private boolean enabled;
    private final transient Set<Role> roles;

    public UserDetailAuth(String email, String password, boolean enabled, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
    }

    public UserDetailAuth(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.roles = user.getRoles();
    }

    @Override
    @Transactional
    public Set<RoleAuth> getAuthorities() {
        return roles.stream()
                .map(role -> new RoleAuth(role.getId(), role.getAuthority()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
