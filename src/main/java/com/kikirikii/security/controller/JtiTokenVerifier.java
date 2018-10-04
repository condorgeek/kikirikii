/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [JtiTokenVerifier.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 09.06.18 11:53
 */

package com.kikirikii.security.controller;

import org.springframework.stereotype.Component;

@Component
public class JtiTokenVerifier {
    public boolean verify(String jti) {
        return true;
    }
}
