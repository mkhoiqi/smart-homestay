package com.rzq.smarthomestay.controller;

import com.rzq.smarthomestay.model.RoomCreateRequest;
import com.rzq.smarthomestay.model.RoomCreateResponse;
import com.rzq.smarthomestay.model.RoomGetResponse;
import com.rzq.smarthomestay.model.WebResponse;
import com.rzq.smarthomestay.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;

    @PostMapping("")
    public WebResponse<RoomCreateResponse> create(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @RequestBody RoomCreateRequest request){
        RoomCreateResponse response = roomService.create(token, request);
        return WebResponse.<RoomCreateResponse>builder()
                .data(response).build();
    }

    @PutMapping("/{id}")
    public WebResponse<RoomCreateResponse> update(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id, @RequestBody RoomCreateRequest request){
        RoomCreateResponse response = roomService.update(token, id, request);
        return WebResponse.<RoomCreateResponse>builder()
                .data(response).build();
    }

    @GetMapping("/{id}")
    public WebResponse<RoomGetResponse> getById(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        RoomGetResponse response = roomService.getById(token, id);
        return WebResponse.<RoomGetResponse>builder()
                .data(response).build();
    }

    @GetMapping("")
    public WebResponse<List<RoomGetResponse>> getAll(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @RequestParam(value = "roomCategoryId", required = false) String roomCategoryId){
        System.out.println("DISININIDNAS");
        List<RoomGetResponse> responses = roomService.getAll(token, roomCategoryId);
        return WebResponse.<List<RoomGetResponse>>builder()
                .data(responses).build();
    }

    @DeleteMapping("{id}")
    public WebResponse<RoomGetResponse> archive(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        RoomGetResponse responses = roomService.archive(token, id);
        return WebResponse.<RoomGetResponse>builder()
                .data(responses).build();
    }

    @PostMapping("/{id}")
    public WebResponse<RoomGetResponse> publish(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        RoomGetResponse responses = roomService.publish(token, id);
        return WebResponse.<RoomGetResponse>builder()
                .data(responses).build();
    }
}
