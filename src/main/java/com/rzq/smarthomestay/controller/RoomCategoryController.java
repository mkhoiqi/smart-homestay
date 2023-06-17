package com.rzq.smarthomestay.controller;


import com.rzq.smarthomestay.model.RoomCategoryCreateRequest;
import com.rzq.smarthomestay.model.RoomCategoryCreateResponse;
import com.rzq.smarthomestay.model.RoomCategoryGetResponse;
import com.rzq.smarthomestay.model.WebResponse;
import com.rzq.smarthomestay.service.RoomCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roomCategories")
public class RoomCategoryController {

    @Autowired
    RoomCategoryService roomCategoryService;
    @PostMapping("")
    public WebResponse<RoomCategoryCreateResponse> create(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @RequestBody RoomCategoryCreateRequest request){
        RoomCategoryCreateResponse response = roomCategoryService.create(token, request);

        return WebResponse.<RoomCategoryCreateResponse>builder()
                .data(response).build();
    }

    @PutMapping("/{id}")
    public WebResponse<RoomCategoryCreateResponse> update(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id, @RequestBody RoomCategoryCreateRequest request){
        RoomCategoryCreateResponse response = roomCategoryService.update(token, id, request);

        return WebResponse.<RoomCategoryCreateResponse>builder()
                .data(response).build();
    }

    @GetMapping("/{id}")
    public WebResponse<RoomCategoryGetResponse> getById(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        RoomCategoryGetResponse response = roomCategoryService.getById(token, id);

        return WebResponse.<RoomCategoryGetResponse>builder()
                .data(response).build();
    }

    @GetMapping("")
    public WebResponse<List<RoomCategoryGetResponse>> getAll(@RequestHeader(value = "X-API-TOKEN", required = false) String token){
        List<RoomCategoryGetResponse> responses = roomCategoryService.getAll(token);

        return WebResponse.<List<RoomCategoryGetResponse>>builder()
                .data(responses).build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<RoomCategoryGetResponse> archive(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        RoomCategoryGetResponse response = roomCategoryService.archive(token, id);

        return WebResponse.<RoomCategoryGetResponse>builder()
                .data(response).build();
    }

    @PostMapping("/{id}")
    public WebResponse<RoomCategoryGetResponse> publish(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        RoomCategoryGetResponse response = roomCategoryService.publish(token, id);

        return WebResponse.<RoomCategoryGetResponse>builder()
                .data(response).build();
    }
}
