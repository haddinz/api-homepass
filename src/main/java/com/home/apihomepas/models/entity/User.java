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
@Table(name = "tbl_users")
public class User {
    @Id
    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 225)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true)
    private String token;

    @Column(name = "token_expired_at", unique = true)
    private Long tokenExpiredAt;

    @OneToMany(mappedBy = "user")
    private List<Contact> contacts;
}
