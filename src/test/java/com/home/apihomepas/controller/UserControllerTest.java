package com.home.apihomepas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.apihomepas.dto.RegisterUserDto;
import com.home.apihomepas.dto.UpdateUserDto;
import com.home.apihomepas.response.UserResponse;
import com.home.apihomepas.response.WebResponse;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.models.repository.UserRepository;
import com.home.apihomepas.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setName("name");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals("OK", response.getData());
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername("");
        dto.setPassword("");
        dto.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("name");

        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername("test");
        dto.setPassword("rahasia");
        dto.setName("name");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetUserUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetUserUnauthorizedTokenNotSend() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setName("name");
        user.setToken("token");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "token")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(response.getData().getUsername(), user.getUsername());
            assertEquals(response.getData().getName(), user.getName());
        });
    }

    @Test
    void testGetUserTokenExpired() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setName("name");
        user.setToken("token");
        user.setTokenExpiredAt(System.currentTimeMillis() - 100000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "token")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserUnauthorized() throws Exception {
        UpdateUserDto dto = new UpdateUserDto();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setName("name");
        user.setToken("token");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000L);
        userRepository.save(user);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("setName");
        dto.setPassword("setPassword");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "token")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals("setName", response.getData().getName());

            User userDb = userRepository.findById("username").orElse(null);
            assertNotNull(userDb);
            assertTrue(BCrypt.checkpw("setPassword", userDb.getPassword()));
        });
    }
}