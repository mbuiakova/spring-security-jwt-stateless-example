package com.example.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RoleAuth implements GrantedAuthority {

    private final int id;
    private final String authority;

    public RoleAuth(int id, @NotBlank @Size(min = 4) String authority) {
        this.id = id;
        this.authority = authority;
    }

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    @Override
    public String getAuthority() {
        return authority;
    }
}
