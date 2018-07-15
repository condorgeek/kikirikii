/*
 * based on http://www.svlada.com/jwt-token-authentication-with-spring-boot/
 */
package com.kikirikii.security.configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

public class CustomCorsFilter extends CorsFilter {

    public CustomCorsFilter(String path) {
        super(configurationSource(path));
    }

    private static UrlBasedCorsConfigurationSource configurationSource(String path) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.setMaxAge(36000L);
        config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(path, config);

        source.registerCorsConfiguration("/public/login", config);
        source.registerCorsConfiguration("/public/token", config);
        source.registerCorsConfiguration("/public/validate/**", config);

        return source;
    }
}