/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */

package com.kikirikii.security.authorization;

import com.kikirikii.security.model.UserContext;
import com.kikirikii.security.token.BearerJwtToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthorizationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 2877954820905567501L;

    private BearerJwtToken bearerToken;
    private UserContext userContext;

    public JwtAuthorizationToken(BearerJwtToken unsafeToken) {
        super(null);
        this.bearerToken = unsafeToken;
        this.setAuthenticated(false);
    }

    public JwtAuthorizationToken(UserContext userContext, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        this.userContext = userContext;
        super.setAuthenticated(true);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return bearerToken;
    }

    @Override
    public Object getPrincipal() {
        return this.userContext;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.bearerToken = null;
    }
}
