package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDetailsResponse {
    private String id;
    private Long price;

    @JsonProperty("room_category")
    private RoomCategoryCreateResponse roomCategory;

}
