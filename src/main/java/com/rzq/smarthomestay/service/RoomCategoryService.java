package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.model.RoomCategoryCreateRequest;
import com.rzq.smarthomestay.model.RoomCategoryCreateResponse;
import com.rzq.smarthomestay.model.RoomCategoryGetResponse;

import java.util.List;

public interface RoomCategoryService {
    public RoomCategoryCreateResponse create(String token, RoomCategoryCreateRequest request);
    public RoomCategoryCreateResponse update(String token, String id, RoomCategoryCreateRequest request);
    public RoomCategoryGetResponse getById(String token, String id);
    public List<RoomCategoryGetResponse> getAll(String token);
    public RoomCategoryGetResponse archive(String token, String id);
    public RoomCategoryGetResponse publish(String token, String id);
}
