package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomCreateRequest {
    @NotNull
    @Min(value = 1)
    @JsonProperty("number_of_rooms")
    private Integer numberOfRooms;

    @NotNull
    @Min(value = 0)
    private Long price;

    @NotBlank
    @JsonProperty("room_category_id")
    private String roomCategoryId;

    @NotNull
    private Set<String> facilities;
}
