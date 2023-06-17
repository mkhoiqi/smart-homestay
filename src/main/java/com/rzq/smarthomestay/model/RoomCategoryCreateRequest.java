package com.rzq.smarthomestay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCategoryCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String name;
}
