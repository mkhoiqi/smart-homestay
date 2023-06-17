package com.rzq.smarthomestay.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    private String id;

    @NotNull
    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @NotNull
    private Long price;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_category_id", referencedColumnName = "id")
    private RoomCategory roomCategory;


    @ManyToMany
    @JoinTable(name = "rooms_facilites",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private Set<Facility> facilities;

    @OneToMany(mappedBy = "room")
    private Set<Transaction> transactions;
}
