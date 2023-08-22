package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.model.*;

import java.util.List;

public interface FacilityService {
    public FacilityCreateResponse create(String token, FacilityCreateRequest request);
    public FacilityCreateResponse update(String token, String id, FacilityCreateRequest request);
    public FacilityGetDetailsResponse getById(String token, String id);
    public List<FacilityGetResponse> getAll(String token);
    public FacilityGetResponse archive(String token, String id);
    public FacilityGetResponse publish(String token, String id);
}
