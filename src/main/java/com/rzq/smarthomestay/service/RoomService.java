package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.model.RoomCreateRequest;
import com.rzq.smarthomestay.model.RoomCreateResponse;
import com.rzq.smarthomestay.model.RoomGetResponse;

import java.util.List;

public interface RoomService {
    public RoomCreateResponse create(String token, RoomCreateRequest request);
    public RoomCreateResponse update(String token, String id, RoomCreateRequest request);
    public RoomGetResponse getById(String token, String id);
    public List<RoomGetResponse> getAll(String token);
    public RoomGetResponse archive(String token, String id);
    public RoomGetResponse publish(String token, String id);
}
