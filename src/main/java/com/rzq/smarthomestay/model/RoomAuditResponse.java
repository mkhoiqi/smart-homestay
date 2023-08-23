package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rzq.smarthomestay.entity.RoomCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAuditResponse {

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("number_of_rooms")
    private Integer numberOfRooms;

    private Long price;

    @JsonProperty("room_category")
    private RoomCategoryCreateResponse roomCategory;

    private Set<FacilityCreateResponse> facilities;
}
