/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [CustomCorsFilter.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 06.09.18 16:50
 */

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
//        source.registerCorsConfiguration("/public/user/**", config);
        source.registerCorsConfiguration("/public/**", config);
        source.registerCorsConfiguration("/stomp/**", config);

        return source;
    }
}