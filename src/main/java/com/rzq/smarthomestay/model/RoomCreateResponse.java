package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rzq.smarthomestay.entity.Room;
import com.rzq.smarthomestay.entity.RoomCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomCreateResponse {
    private String id;

    @JsonProperty("room_number")
    private String roomNumber;

    private Long price;

    @JsonProperty("room_category")
    private RoomCategory roomCategory;
}
