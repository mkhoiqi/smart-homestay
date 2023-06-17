package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rzq.smarthomestay.entity.RoomCategory;

import java.time.LocalDateTime;

public class RoomGetResponse {
    private String id;

    @JsonProperty("room_number")
    private String roomNumber;

    private Long price;

    @JsonProperty("room_category")
    private RoomCategory roomCategory;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}
