package com.home.apihomepas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String name;
}
