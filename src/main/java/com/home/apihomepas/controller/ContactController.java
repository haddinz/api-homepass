package com.home.apihomepas.controller;

import com.home.apihomepas.dto.CreateContactDto;
import com.home.apihomepas.dto.SearchContactDto;
import com.home.apihomepas.dto.UpdateContactDto;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.response.ContactResponse;
import com.home.apihomepas.response.PagingResponse;
import com.home.apihomepas.response.WebResponse;
import com.home.apihomepas.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(
            path = "/api/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactDto dto) {
        ContactResponse contactResponse = contactService.create(user, dto);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @GetMapping(
            path = "/api/contact/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
        ContactResponse contactResponse = contactService.get(user, contactId);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @PutMapping(
            path = "/api/contact/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> update(User user, @RequestBody UpdateContactDto dto, @PathVariable("contactId") String contactId) {
        dto.setId(contactId);

        ContactResponse contactResponse = contactService.update(user, dto);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @DeleteMapping(
            path = "/api/contact/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable("contactId") String contactId) {
        contactService.delete(user, contactId);
        return WebResponse.<String>builder().data("Delete Successfully").build();
    }

    @GetMapping(
            path = "/api/contact",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(
            User user,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        SearchContactDto dto = SearchContactDto.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .page(page)
                .size(size)
                .build();

        Page<ContactResponse> contactResponses = contactService.search(user, dto);
        return WebResponse.<List<ContactResponse>>builder()
                .data(contactResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(contactResponses.getNumber())
                        .totalPage(contactResponses.getTotalPages())
                        .size(contactResponses.getSize())
                        .build())
                .build();
    }
}
