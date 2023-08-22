package com.rzq.smarthomestay.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "additional_facilities_audit")
public class AdditionalFacilityAudit {
    @Id
    private String id;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private Long price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "additional_facility_id", referencedColumnName = "id")
    private AdditionalFacility additionalFacility;
}
