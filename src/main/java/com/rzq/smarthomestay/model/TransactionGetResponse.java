package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionGetResponse {
    private String id;
    private Long amount;

    @JsonProperty("checkin_date")
    private LocalDate checkinDate;

    @JsonProperty("checkout_date")
    private LocalDate checkoutDate;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("number_of_rooms")
    private Integer numberOfRooms;
    private String status;

    @JsonProperty("created_by")
    private UserDetailsResponse createdBy;
    private RoomDetailsResponse room;
}
