package com.home.apihomepas.service;

import com.home.apihomepas.dto.CreateContactDto;
import com.home.apihomepas.dto.SearchContactDto;
import com.home.apihomepas.dto.UpdateContactDto;
import com.home.apihomepas.models.entity.Contact;
import com.home.apihomepas.models.entity.User;
import com.home.apihomepas.models.repository.ContactRepository;
import com.home.apihomepas.response.ContactResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactService {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public ContactResponse create(User user, CreateContactDto dto) {
        validationService.validation(dto);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(dto.getFirstname());
        contact.setLastName(dto.getLastname());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setUser(user);

        return contactResponse(contact);
    }

    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact id not found"));

        return contactResponse(contact);
    }

    @Transactional
    public ContactResponse update(User user, UpdateContactDto dto) {
        validationService.validation(dto);

        Contact contact = contactRepository.findFirstByUserAndId(user, dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));

        contact.setFirstName(dto.getFirstname());
        contact.setLastName(dto.getLastname());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contactRepository.save(contact);

        return contactResponse(contact);
    }

    @Transactional
    public void delete(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));

        contactRepository.delete(contact);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactDto dto) {
        Specification<Contact> specification = ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));
            if(Objects.nonNull(dto.getName())) {
                predicates.add(builder.or(
                   builder.like(root.get("firstName"), "%"+ dto.getName() +"%"),
                   builder.like(root.get("lastName"), "%"+ dto.getName() +"%")
                ));
            }
            if(Objects.nonNull(dto.getEmail())) {
                predicates.add(builder.like(root.get("email"), "%"+ dto.getEmail() +"%"));
            }
            if(Objects.nonNull(dto.getPhone())) {
                predicates.add(builder.like(root.get("phone"), "%"+ dto.getPhone() +"%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        });

        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream()
                .map(this::contactResponse)
                .toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
    }

    private ContactResponse contactResponse(Contact contact){
        return ContactResponse.builder()
                .id(contact.getId())
                .firstname(contact.getFirstName())
                .lastname(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

}
