package com.home.apihomepas.service;

import com.home.apihomepas.dto.CreateAddressDto;
import com.home.apihomepas.dto.UpdateAddressDto;
import com.home.apihomepas.models.entity.Address;
import com.home.apihomepas.models.entity.Contact;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.models.repository.AddressRepository;
import com.home.apihomepas.models.repository.ContactRepository;
import com.home.apihomepas.response.AddressResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public AddressResponse create(User user, CreateAddressDto dto) {
        validationService.validation(dto);

        Contact contact = contactRepository.findFirstByUserAndId(user, dto.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setProvince(dto.getProvince());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        addressRepository.save(address);

        return addressResponse(address);
    }

    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        return addressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressDto dto) {
        validationService.validation(dto);

        Contact contact = contactRepository.findFirstByUserAndId(user, dto.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, dto.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setProvince(dto.getProvince());
        address.setPostalCode(dto.getPostalCode());
        addressRepository.save(address);

        return addressResponse(address);
    }

    @Transactional
    public void remove(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact is not found"));

        List<Address> addresses = addressRepository.findAllByContact(contact);
        return addresses.stream().map(this::addressResponse).toList();
    }

    private AddressResponse addressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
