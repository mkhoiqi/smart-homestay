package com.rzq.smarthomestay.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private String id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "username")
    private User createdBy;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @NotNull
    private String status;

    @NotNull
    private String lastAction;

    @NotNull
    private String lastActivity;

    @ManyToOne
    @JoinColumn(name = "pending_user", referencedColumnName = "username")
    private User pendingUser;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @NotNull
    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @NotNull
    @Column(name = "checkin_date")
    private LocalDate checkinDate;

    @NotNull
    @Column(name = "checkout_date")
    private LocalDate checkoutDate;

    @NotNull
    private Long amount;

    @ManyToMany
    @JoinTable(name = "transaction_additional_facilities",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "additional_facility_id"))
    private Set<AdditionalFacility> additionalFacilities;

    @OneToMany(mappedBy = "transaction")
    private Set<Audit> audits;
}
