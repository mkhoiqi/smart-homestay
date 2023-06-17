package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityGetResponse {
    private String id;

    private String name;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
}
