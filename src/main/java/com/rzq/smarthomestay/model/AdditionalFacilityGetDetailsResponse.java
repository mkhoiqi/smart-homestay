package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdditionalFacilityGetDetailsResponse {
    private String id;
    private String name;
    private Long price;
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
    private Set<AdditionalFacilityAuditResponse> audits;
}
