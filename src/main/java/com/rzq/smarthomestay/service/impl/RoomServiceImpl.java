package com.rzq.smarthomestay.service.impl;

import com.rzq.smarthomestay.model.RoomCreateRequest;
import com.rzq.smarthomestay.model.RoomCreateResponse;
import com.rzq.smarthomestay.model.RoomGetResponse;
import com.rzq.smarthomestay.service.RoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Override
    public RoomCreateResponse create(String token, RoomCreateRequest request) {
        return null;
    }

    @Override
    public RoomCreateResponse update(String token, String id, RoomCreateRequest request) {
        return null;
    }

    @Override
    public RoomGetResponse getById(String token, String id) {
        return null;
    }

    @Override
    public List<RoomGetResponse> getAll(String token) {
        return null;
    }

    @Override
    public RoomGetResponse archive(String token, String id) {
        return null;
    }

    @Override
    public RoomGetResponse publish(String token, String id) {
        return null;
    }
}
