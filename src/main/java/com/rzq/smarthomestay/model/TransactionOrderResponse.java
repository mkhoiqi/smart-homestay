package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rzq.smarthomestay.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionOrderResponse {
    private String id;
    private Long amount;

    @JsonProperty("checkin_date")
    private LocalDate checkinDate;

    @JsonProperty("checkout_date")
    private LocalDate checkoutDate;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("last_action")
    private String lastAction;

    @JsonProperty("last_activity")
    private String lastActivity;

    @JsonProperty("number_of_rooms")
    private Integer numberOfRooms;
    private String status;

    @JsonProperty("created_by")
    private UserDetailsResponse createdBy;
    private RoomDetailsResponse room;

    @JsonProperty("additional_facilities")
    private Set<AdditionalFacilityCreateResponse> additionalFacilities;
}
