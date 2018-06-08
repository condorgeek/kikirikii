/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */
package com.kikirikii.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kikirikii.exceptions.AuthMethodNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;


public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
                                   AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        if (!"POST".equals(request.getMethod()) || !isAjax.apply(request)) {
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        UsernamePassword login = objectMapper.readValue(request.getReader(), UsernamePassword.class);

        if (isBlank.apply(login.getUsername()) || isBlank.apply(login.getPassword())) {
            throw new AuthenticationServiceException("Username or Password not provided");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());

        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    static private Function<String, Boolean> isBlank = s -> s == null || s.length() == 0;
    static private Function<HttpServletRequest, Boolean> isAjax = request -> "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));


    static class UsernamePassword {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

