package com.home.apihomepas.service;

import com.home.apihomepas.dto.LoginUserDto;
import com.home.apihomepas.response.TokenResponse;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.models.repository.UserRepository;
import com.home.apihomepas.security.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserDto dto) {
        validationService.validation(dto);

        User user = userRepository.findById(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong password and username"));

        if(BCrypt.checkpw(user.getPassword(), dto.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder().
                    token(user.getToken()).
                    expiredAt(user.getTokenExpiredAt()).
                    build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong password and username");
        }
    }

    private Long next30Days() {
        return System.currentTimeMillis() + (1000 + 16 + 24 + 30);
    }

    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }
}
