package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.model.*;

public interface AdditionalFacilityService {
    public AdditionalFacilityCreateResponse create(String token, AdditionalFacilityCreateRequest request);
    public AdditionalFacilityCreateResponse update(String token, String id, AdditionalFacilityCreateRequest request);
    public AdditionalFacilityGetResponse getById(String token, String id);
    public AdditionalFacilityGetResponse archive(String token, String id);
    public AdditionalFacilityGetResponse publish(String token, String id);
}
