package com.kikirikii.controllers;

import com.kikirikii.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/public")
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return null;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout() {
    }

}
