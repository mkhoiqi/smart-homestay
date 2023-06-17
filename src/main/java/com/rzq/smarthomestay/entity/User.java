package com.rzq.smarthomestay.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @NotNull
    @Column(length = 20)
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Column(length = 50)
    private String name;

    private String token;

    @Column(name = "token_expired_at")
    private Long tokenExpiredAt;

    @NotNull
    @Column(name = "is_employees")
    private Boolean isEmployees;

    @OneToMany(mappedBy = "createdBy")
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "pendingUser")
    private Set<Transaction> transactionsPending;

    @OneToMany(mappedBy = "createdBy")
    private Set<Audit> audits;

}
