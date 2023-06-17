package com.rzq.smarthomestay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdditionalFacilityCreateResponse {
    private String id;
    private String name;
    private Long price;
}
