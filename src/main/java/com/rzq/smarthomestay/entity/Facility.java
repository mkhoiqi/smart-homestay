package com.rzq.smarthomestay.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "facilities")
public class Facility {
    @Id
    private String id;

    @NotNull
    @Column(length = 50)
    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany(mappedBy = "facilities")
    //pakai set karena unique
    private Set<Room> rooms;

    @OneToMany(mappedBy = "facility")
    private Set<FacilityAudit> audits;


    @ManyToMany(mappedBy = "facilities")
    //pakai set karena unique
    private Set<RoomAudit> roomsAudit;
}
