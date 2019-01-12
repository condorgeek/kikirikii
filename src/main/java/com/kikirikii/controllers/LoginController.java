/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [LoginController.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 04.10.18 11:34
 */

package com.kikirikii.controllers;

import com.kikirikii.model.User;
import com.kikirikii.model.dto.Site;
import com.kikirikii.model.dto.UserRequest;
import com.kikirikii.services.UserService;
import com.kikirikii.storage.SiteProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/public")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private SiteProperties configuration;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return null;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout() {
    }

    @RequestMapping(value = "/validate/email", method = RequestMethod.GET)
    public boolean emailExists(@RequestParam("value") String email) {
        return userService.findByEmail(email).isPresent();
    }

    @RequestMapping(value = "/validate/username", method = RequestMethod.GET)
    public boolean usernameExists(@RequestParam("value") String username) {
        return userService.findByUsername(username).isPresent();
    }

    @RequestMapping(value = "/user/create/{username}", method = RequestMethod.POST)
    public User createUser(@PathVariable String username, @RequestBody UserRequest userRequest) {

        User user = userService.createUser(username, userRequest);
        userService.createPublicSpaces(user);

        return user;
    }

    @RequestMapping(value = "/app/configuration", method = RequestMethod.GET)
    public Site getSiteConfiguration() {
        return configuration.getSiteConfiguration();
    }

}
