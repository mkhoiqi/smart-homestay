package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdditionalFacilityGetResponse {
    private String id;
    private String name;
    private Long price;
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}
