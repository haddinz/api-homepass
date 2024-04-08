package com.home.apihomepas.service;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;
import java.util.Set;

@Service
public class ValidationService {

    @Autowired
    private Validator validator;

    public void validation(Object request) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
        if(constraintViolations != null) {
            throw new ConcurrentModificationException(String.valueOf(constraintViolations));
        }
    }
}
