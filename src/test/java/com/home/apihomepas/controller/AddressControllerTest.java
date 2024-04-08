package com.home.apihomepas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.apihomepas.dto.CreateAddressDto;
import com.home.apihomepas.dto.UpdateAddressDto;
import com.home.apihomepas.models.entity.Address;
import com.home.apihomepas.models.entity.Contact;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.models.repository.AddressRepository;
import com.home.apihomepas.models.repository.ContactRepository;
import com.home.apihomepas.models.repository.UserRepository;
import com.home.apihomepas.response.AddressResponse;
import com.home.apihomepas.response.WebResponse;
import com.home.apihomepas.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("test");
        contact.setUser(user);
        contact.setFirstName("firstName");
        contact.setLastName("lastName");
        contact.setEmail("email@example.com");
        contact.setPhone("092193108237");
        contactRepository.save(contact);
    }

    @Test
    void createAddressBadRequest() throws Exception {
        CreateAddressDto dto = new CreateAddressDto();
        dto.setCountry("");

        mockMvc.perform(
                post("/api/contact/test/address")
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
    void createAddressSuccess() throws Exception {
        CreateAddressDto dto = new CreateAddressDto();
        dto.setCity("city");
        dto.setStreet("street");
        dto.setCountry("country");
        dto.setProvince("province");
        dto.setPostalCode("postalCode");

        mockMvc.perform(
                post("/api/contact/test/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(dto.getCity(), response.getData().getCity());
            assertEquals(dto.getStreet(), response.getData().getStreet());
            assertEquals(dto.getCountry(), response.getData().getCountry());
            assertEquals(dto.getProvince(), response.getData().getPostalCode());
            assertEquals(dto.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact/test/address/test")
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
    void getAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Jalan");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setCountry("Indonesia");
        address.setPostalCode("123123");
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contact/test/address/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(address.getCity(), response.getData().getCity());
            assertEquals(address.getStreet(), response.getData().getStreet());
            assertEquals(address.getCountry(), response.getData().getCountry());
            assertEquals(address.getProvince(), response.getData().getProvince());
            assertEquals(address.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void updateAddressBadRequest() throws Exception {
        UpdateAddressDto dto = new UpdateAddressDto();
        dto.setCountry("");

        mockMvc.perform(
                put("/api/contact/test/address/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Lama");
        address.setCity("Lama");
        address.setProvince("Lama");
        address.setCountry("Lama");
        address.setPostalCode("43535");
        addressRepository.save(address);

        UpdateAddressDto dto = new UpdateAddressDto();
        dto.setStreet("Jalan");
        dto.setCity("Jakarta");
        dto.setCountry("Indonesia");
        dto.setProvince("DKI");
        dto.setPostalCode("123123");

        mockMvc.perform(
                put("/api/contact/test/address/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals(dto.getStreet(), response.getData().getStreet());
            assertEquals(dto.getCity(), response.getData().getCity());
            assertEquals(dto.getProvince(), response.getData().getProvince());
            assertEquals(dto.getCountry(), response.getData().getCountry());
            assertEquals(dto.getPostalCode(), response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void deleteAddressNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/contact/test/address/test")
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
    void deleteAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setContact(contact);
        address.setStreet("Jalan");
        address.setCity("Jakarta");
        address.setProvince("DKI");
        address.setCountry("Indonesia");
        address.setPostalCode("123123");
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contact/test/address/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals("address remove successfully", response.getData());
            assertFalse(addressRepository.existsById("test"));
        });
    }

    @Test
    void listAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact/salah/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void listAddressSuccess() throws Exception {
        Contact contact = contactRepository.findById("test").orElseThrow();

        for (int i = 0; i < 5; i++) {
            Address address = new Address();
            address.setId("test-" + i);
            address.setContact(contact);
            address.setStreet("Jalan");
            address.setCity("Jakarta");
            address.setProvince("DKI");
            address.setCountry("Indonesia");
            address.setPostalCode("123123");
            addressRepository.save(address);
        }

        mockMvc.perform(
                get("/api/contact/test/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNull(response.getErrors());
            assertEquals(5, response.getData().size());
        });
    }
}