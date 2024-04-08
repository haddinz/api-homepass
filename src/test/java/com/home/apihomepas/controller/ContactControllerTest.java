package com.home.apihomepas.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.apihomepas.dto.CreateContactDto;
import com.home.apihomepas.dto.UpdateContactDto;
import com.home.apihomepas.models.entity.Contact;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.models.repository.ContactRepository;
import com.home.apihomepas.models.repository.UserRepository;
import com.home.apihomepas.response.ContactResponse;
import com.home.apihomepas.response.WebResponse;
import com.home.apihomepas.security.BCrypt;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
        userRepository.save(user);
    }

    @Test
    void createContactBadRequest() throws Exception {
        CreateContactDto dto = new CreateContactDto();
        dto.setFirstname("");
        dto.setEmail("email");

        mockMvc.perform(
                post("/api/contact")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void createContactSuccess() throws Exception {
        CreateContactDto dto = new CreateContactDto();
        dto.setFirstname("cuy");
        dto.setLastname("universe");
        dto.setEmail("email@gmail.com");
        dto.setPhone("0852898932");

        mockMvc.perform(
                post("/api/contact")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals("cuy", dto.getFirstname());
            assertEquals("universe", dto.getLastname());
            assertEquals("email@gmail.com", dto.getEmail());
            assertEquals("0852898932", dto.getPhone());
            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getContactNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact/09388821")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getContactSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("firstname");
        contact.setLastName("lastname");
        contact.setEmail("email@example.com");
        contact.setPhone("087708932713");
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());

            assertEquals(contact.getId(), response.getData().getId());
            assertEquals(contact.getFirstName(), response.getData().getFirstname());
            assertEquals(contact.getLastName(), response.getData().getLastname());
            assertEquals(contact.getEmail(), response.getData().getEmail());
            assertEquals(contact.getPhone(), response.getData().getPhone());
        });
    }

    @Test
    void updateContactBadRequest() throws Exception {
        UpdateContactDto dto = new UpdateContactDto();
        dto.setFirstname("");
        dto.setEmail("email");

        mockMvc.perform(
                put("/api/contact/saedase")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateContactSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("firstname");
        contact.setLastName("lastname");
        contact.setEmail("email@example.com");
        contact.setPhone("087708932713");
        contactRepository.save(contact);

        CreateContactDto dto = new CreateContactDto();
        contact.setFirstName("Daruss");
        contact.setLastName("nama belakang");
        contact.setEmail("rubahemail@example.com");
        contact.setPhone("0877090832713");

        mockMvc.perform(
                put("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());

            assertEquals(dto.getFirstname(), response.getData().getFirstname());
            assertEquals(dto.getLastname(), response.getData().getLastname());
            assertEquals(dto.getEmail(), response.getData().getEmail());
            assertEquals(dto.getPhone(), response.getData().getPhone());
        });
    }

    @Test
    void deleteContactNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contact/09388821")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteContactSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("firstname");
        contact.setLastName("lastname");
        contact.setEmail("email@example.com");
        contact.setPhone("087708932713");
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals("Delete Successfully", response.getData());
        });
    }

    @Test
    void searchNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(0, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }

    @Test
    void searchSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        for (int i = 0; i < 100; i++) {
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("leo" + i);
            contact.setLastName("lastname");
            contact.setEmail("email" + i + "@example.com");
            contact.setPhone("087708932713");
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contact")
                        .queryParam("name", "leo")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contact")
                        .queryParam("name", "leo")
                        .queryParam("page", "1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
            assertEquals(10, response.getPaging().getTotalPage());
            assertEquals(1000, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }
}