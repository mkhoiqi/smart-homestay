package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomCreateRequest {
    @NotBlank
    @Size(min = 1, max = 10)
    @JsonProperty("room_number")
    private String roomNumber;

    @NotBlank
    @Min(value = 0)
    private Long price;

    @NotBlank
    @JsonProperty("room_category_id")
    private String roomCategoryId;
}
