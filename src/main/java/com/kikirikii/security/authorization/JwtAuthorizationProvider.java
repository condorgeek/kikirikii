/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [JwtAuthorizationProvider.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 22.07.18 13:58
 */

/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */

package com.kikirikii.security.authorization;

import com.kikirikii.security.configuration.SecurityProperties;
import com.kikirikii.security.model.UserContext;
import com.kikirikii.security.token.BearerJwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("unchecked")
public class JwtAuthorizationProvider implements AuthenticationProvider {
    private final SecurityProperties securitySettings;
    
    @Autowired
    public JwtAuthorizationProvider(SecurityProperties securitySettings) {
        this.securitySettings = securitySettings;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        BearerJwtToken rawAccessToken = (BearerJwtToken) authentication.getCredentials();

        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(securitySettings.getTokenSigningKey());
        String subject = jwsClaims.getBody().getSubject();
        List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
        List<GrantedAuthority> authorities = scopes.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        UserContext context = UserContext.of(subject, authorities);
        
        return new JwtAuthorizationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthorizationToken.class.isAssignableFrom(authentication));
    }
}
