package com.rzq.smarthomestay.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "additional_facilities")
public class AdditionalFacility {
    @Id
    @NotNull
    private String id;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private Long price;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
