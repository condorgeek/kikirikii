package com.kikirikii.security.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.function.Function;

/**
 * @author vladimir.stankovic
 * <p>
 * Aug 4, 2016
 */
public class UserContext {
    private final String username;
    private final List<GrantedAuthority> authorities;

    private UserContext(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public static UserContext create(String username, List<GrantedAuthority> authorities) {
        if (isBlank.apply(username)) throw new IllegalArgumentException("Username is blank: " + username);
        return new UserContext(username, authorities);
    }

    public String getUsername() {
        return username;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    static private Function<String, Boolean> isBlank = s -> s == null || s.length() == 0;
}


