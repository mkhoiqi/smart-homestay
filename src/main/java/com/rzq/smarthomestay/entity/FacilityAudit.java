package com.rzq.smarthomestay.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facilities_audit")
public class FacilityAudit {
    @Id
    private String id;

    @NotNull
    @Column(length = 50)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private Facility facility;
}
