package com.kikirikii.controllers;

import com.kikirikii.model.User;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/public")
public class LoginController {

    @Autowired
    private UserService userService;

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

}
