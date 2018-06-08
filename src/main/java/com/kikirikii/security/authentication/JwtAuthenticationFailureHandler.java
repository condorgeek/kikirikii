/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */

package com.kikirikii.security.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kikirikii.exceptions.AuthMethodNotSupportedException;
import com.kikirikii.exceptions.ExpiredTokenException;
import com.kikirikii.security.model.ErrorCode;
import com.kikirikii.security.model.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (e instanceof BadCredentialsException) {
            mapper.writeValue(response.getWriter(), ErrorResponse.of("Invalid username or password", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (e instanceof ExpiredTokenException) {
            mapper.writeValue(response.getWriter(), ErrorResponse.of("Token has expired", ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
        } else if (e instanceof AuthMethodNotSupportedException) {
            mapper.writeValue(response.getWriter(), ErrorResponse.of(e.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        }

        mapper.writeValue(response.getWriter(), ErrorResponse.of("Authentication failed", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
    }
}
