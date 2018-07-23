/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */

package com.kikirikii.security.model;

import com.kikirikii.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.function.Function;

public class UserContext {
    private final String username;
    private User user;
    private final List<GrantedAuthority> authorities;

    private UserContext(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public static UserContext of(User user, List<GrantedAuthority> authorities) {
       UserContext userContext = new UserContext(user.getUsername(), authorities);
       userContext.user = user;
       return userContext;
    }

    public static UserContext of(String username, List<GrantedAuthority> authorities) {
        if (isBlank.apply(username)) throw new IllegalArgumentException("Username is blank: " + username);
        return new UserContext(username, authorities);
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    static private Function<String, Boolean> isBlank = s -> s == null || s.length() == 0;
}


