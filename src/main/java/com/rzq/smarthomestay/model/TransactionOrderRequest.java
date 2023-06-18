package com.rzq.smarthomestay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionOrderRequest {
    @NotNull
    @JsonProperty("checkin_date")
    private LocalDate checkinDate;

    @NotNull
    @JsonProperty("checkout_date")
    private LocalDate checkoutDate;

    @NotNull
    @JsonProperty("number_of_rooms")
    private Integer numberOfRooms;

    @NotBlank
    @JsonProperty("room_id")
    private String roomId;

    @NotNull
    @JsonProperty("additional_facilities")
    private Set<String> additionalFacilities;
}
