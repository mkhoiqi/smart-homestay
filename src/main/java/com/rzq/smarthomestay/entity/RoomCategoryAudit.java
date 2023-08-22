package com.rzq.smarthomestay.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_categories_audit")
public class RoomCategoryAudit {
    @Id
    private String id;

    @NotNull
    @Column(length = 50)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "room_category_id", referencedColumnName = "id")
    private RoomCategory roomCategory;
}
