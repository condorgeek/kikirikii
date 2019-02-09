/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [Pages.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 09.02.19 16:44
 */

package com.kikirikii.model.enums;

/**
 *  standard page names - reserved
 */

@Deprecated
public enum Pages {
    CONTACT("contact"), IMPRINT("imprint"), TERMS_OF_USE("terms-of-use"),
    PRIVACY_POLICY("privacy-policy"), NOT_FOUND("not-found");

    String name;

    Pages(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}
