/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [BearerJwtToken.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 09.06.18 11:36
 */

/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */
package com.kikirikii.security.token;

import com.kikirikii.exceptions.ExpiredTokenException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.function.Function;

public class BearerJwtToken implements JwtToken {
    private static Logger logger = LoggerFactory.getLogger(BearerJwtToken.class);

    private String bearerToken;

    public BearerJwtToken(String bearerHeader) {
        this.bearerToken = extract.apply(bearerHeader);
    }

    public Jws<Claims> parseClaims(String signingKey) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.bearerToken);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            logger.error("Invalid JWT Token", ex);
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            logger.info("JWT Token is expired", expiredEx);
            throw new ExpiredTokenException(this, "JWT Token expired", expiredEx);
        }
    }

    @Override
    public String getToken() {
        return bearerToken;
    }

    static private Function<String, Boolean> isBlank = s -> s == null || s.length() == 0;
    static private Function<String, String> extract = header -> {
        try {
            if(isBlank.apply(header)) {
                throw new AuthenticationServiceException("Authorization header malformed.");
            }
            return header.substring("Bearer ".length(), header.length());
        } catch (Exception e) {
            throw new AuthenticationServiceException("Authorization header invalid.");
        }
    };
}
