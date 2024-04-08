package com.home.apihomepas.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_addresses")
public class Address {
    @Id
    @Column(nullable = false, length = 100)
    private String id;

    @Column(length = 200)
    private String street;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private Contact contact;
}
