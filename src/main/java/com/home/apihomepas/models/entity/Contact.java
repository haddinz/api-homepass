package com.home.apihomepas.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_contacts")
public class Contact {
    @Id
    @Column(nullable = false, length = 100)
    private String id;

    @Column(name = "first_name" ,nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name" ,nullable = false, length = 100)
    private String lastName;
    private String phone;
    private String email;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @OneToMany(mappedBy = "contact")
    private List<Address> addresses;
}
