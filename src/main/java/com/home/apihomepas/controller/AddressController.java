package com.home.apihomepas.controller;

import com.home.apihomepas.dto.CreateAddressDto;
import com.home.apihomepas.dto.UpdateAddressDto;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.response.AddressResponse;
import com.home.apihomepas.response.WebResponse;
import com.home.apihomepas.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(
            path = "/api/contact/{contactId}/address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(
            User user,
            @RequestBody CreateAddressDto dto,
            @PathVariable("contactId") String contactId) {
        dto.setContactId(contactId);
        AddressResponse addressResponse = addressService.create(user, dto);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(
            path = "/api/contact/{contactId}/address/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(
            User user,
            @PathVariable("contactId") String contactId,
            @PathVariable("addressId") String addressId
    ) {
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PutMapping(
            path = "/api/contact/{contactId}/address/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(
            User user,
            @RequestBody UpdateAddressDto dto,
            @PathVariable("contactId") String contactId,
            @PathVariable("addressId") String addressId) {
        dto.setContactId(contactId);
        dto.setAddressId(addressId);

        AddressResponse addressResponse = addressService.update(user, dto);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @DeleteMapping(
            path = "/api/contact/{contactId}/address/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> remove(
            User user,
            @PathVariable("contactId") String contactId,
            @PathVariable("addressId") String addressId) {

        addressService.remove(user, contactId, addressId);
        return WebResponse.<String>builder().data("address remove successfully").build();
    }

    @GetMapping(
            path = "/api/contact/{contactId}/address",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> list(User user, @PathVariable("contactId") String contactId) {
        List<AddressResponse> addressResponses = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder().data(addressResponses).build();
    }
}
