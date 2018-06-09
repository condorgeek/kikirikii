package com.kikirikii.security.controller;

import org.springframework.stereotype.Component;

@Component
public class JtiTokenVerifier {
    public boolean verify(String jti) {
        return true;
    }
}
