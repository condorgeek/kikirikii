/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */

package com.kikirikii.security.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kikirikii.security.authentication.JwtAuthenticationProvider;
import com.kikirikii.security.authentication.JwtAuthenticationFilter;
import com.kikirikii.security.authorization.JwtAuthorizationProvider;
import com.kikirikii.security.authorization.JwtAuthorizationFilter;
import com.kikirikii.security.util.TokenExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    public static final String AUTHENTICATION_URL = "/public/login";
    public static final String REFRESH_TOKEN_URL = "/public/token";
    public static final String SECURE_ROOT_URL = "/user/**";

    @Autowired
    private DefaultEntryPoint defaultEntryPoint;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private JwtAuthorizationProvider jwtAuthorizationProvider;

    @Autowired
    private TokenExtractor tokenExtractor;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    protected JwtAuthenticationFilter buildJwtAuthenticationFilter(String loginEntryPoint) throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(loginEntryPoint, successHandler, failureHandler, objectMapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    protected JwtAuthorizationFilter buildJwtAuthorizationFilter(List<String> pathsToSkip, String pattern) throws Exception {
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
        JwtAuthorizationFilter filter
                = new JwtAuthorizationFilter(failureHandler, tokenExtractor, matcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider);
        auth.authenticationProvider(jwtAuthorizationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> permitAllEndpointList = Arrays.asList(
                AUTHENTICATION_URL,
                REFRESH_TOKEN_URL,
                "/console"
        );

        http
                .csrf().disable() // We don't need CSRF for JWT based authentication
                .exceptionHandling()
                .authenticationEntryPoint(this.defaultEntryPoint)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()]))
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers(SECURE_ROOT_URL).authenticated() // Protected API End-points
                .and()
                .addFilterBefore(new CustomCorsFilter(SECURE_ROOT_URL), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtAuthenticationFilter(AUTHENTICATION_URL), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtAuthorizationFilter(permitAllEndpointList,
                        SECURE_ROOT_URL), UsernamePasswordAuthenticationFilter.class);
    }
}
