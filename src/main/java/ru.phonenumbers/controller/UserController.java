package ru.phonenumbers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.phonenumbers.dto.UserResponse;
import ru.phonenumbers.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user/{id}")
    public UserResponse getUser(@PathVariable("id") String id) {
        return userService.getUser(id);
    }
}
