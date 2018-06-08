package com.kikirikii.security.util;

/**
 * 
 * @author vladimir.stankovic
 *
 * Aug 17, 2016
 */
public interface TokenVerifier {
    public boolean verify(String jti);
}
