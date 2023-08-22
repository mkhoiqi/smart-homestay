package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rzq.smarthomestay.entity.RoomCategoryAudit;
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
public class RoomCategoryGetDetailsResponse {
    private String id;
    private String name;
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
    private Set<RoomCategoryAuditResponse> audits;
}
