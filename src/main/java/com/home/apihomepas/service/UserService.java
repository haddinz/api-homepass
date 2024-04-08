package com.home.apihomepas.service;

import com.home.apihomepas.dto.RegisterUserDto;
import com.home.apihomepas.dto.UpdateUserDto;
import com.home.apihomepas.response.UserResponse;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.models.repository.UserRepository;
import com.home.apihomepas.security.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userReposiroty;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void regiter(RegisterUserDto dto) {
        validationService.validation(dto);

        if(userReposiroty.existsById(dto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exist");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));

        userReposiroty.save(user);
    }

    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserDto dto) {
        validationService.validation(dto);

        if(Objects.nonNull(dto.getName())) {
            user.setName(dto.getName());
        }

        if(Objects.nonNull(dto.getPassword())) {
            user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        }

        userReposiroty.save(user);

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }
}
