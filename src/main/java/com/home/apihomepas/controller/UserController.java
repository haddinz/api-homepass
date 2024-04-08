package com.home.apihomepas.controller;

import com.home.apihomepas.dto.RegisterUserDto;
import com.home.apihomepas.dto.UpdateUserDto;
import com.home.apihomepas.response.UserResponse;
import com.home.apihomepas.response.WebResponse;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserDto dto) {
        userService.regiter(dto);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user){
        UserResponse userResponse = userService.get(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserDto dto) {
        UserResponse userResponse = userService.update(user, dto);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }
}
