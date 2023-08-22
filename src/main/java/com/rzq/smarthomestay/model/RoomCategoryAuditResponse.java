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
public class RoomCategoryAuditResponse {
    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
