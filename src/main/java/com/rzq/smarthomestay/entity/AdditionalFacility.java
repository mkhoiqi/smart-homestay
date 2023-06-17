package com.rzq.smarthomestay.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "additional_facilities")
public class AdditionalFacility {
    @Id
    private String id;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    @Min(1)
    private Long price;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany(mappedBy = "additionalFacilities")
    private Set<Transaction> transactions;
}
