package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.model.*;

import java.util.List;

public interface AdditionalFacilityService {
    public AdditionalFacilityCreateResponse create(String token, AdditionalFacilityCreateRequest request);
    public AdditionalFacilityCreateResponse update(String token, String id, AdditionalFacilityCreateRequest request);
    public AdditionalFacilityGetDetailsResponse getById(String token, String id);
    public List<AdditionalFacilityGetResponse> getAll(String token);
    public AdditionalFacilityGetResponse archive(String token, String id);
    public AdditionalFacilityGetResponse publish(String token, String id);
}
