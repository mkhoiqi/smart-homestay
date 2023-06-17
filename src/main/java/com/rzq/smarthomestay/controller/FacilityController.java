package com.rzq.smarthomestay.controller;

import com.rzq.smarthomestay.model.FacilityCreateRequest;
import com.rzq.smarthomestay.model.FacilityCreateResponse;
import com.rzq.smarthomestay.model.FacilityGetResponse;
import com.rzq.smarthomestay.model.WebResponse;
import com.rzq.smarthomestay.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityController {
    @Autowired
    FacilityService facilityService;

    @PostMapping("")
    public WebResponse<FacilityCreateResponse> create(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @RequestBody FacilityCreateRequest request){
        FacilityCreateResponse response = facilityService.create(token, request);
        return WebResponse.<FacilityCreateResponse>builder()
                .data(response).build();
    }


    @PutMapping("/{id}")
    public WebResponse<FacilityCreateResponse> update(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @RequestBody FacilityCreateRequest request, @PathVariable("id") String id){
        FacilityCreateResponse response = facilityService.update(token, id, request);
        return WebResponse.<FacilityCreateResponse>builder()
                .data(response).build();
    }

    @GetMapping("/{id}")
    public WebResponse<FacilityGetResponse> getById(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        FacilityGetResponse response = facilityService.getById(token, id);
        return WebResponse.<FacilityGetResponse>builder()
                .data(response).build();
    }

    @GetMapping("/")
    public WebResponse<List<FacilityGetResponse>> getAll(@RequestHeader(value = "X-API-TOKEN", required = false) String token){
        List<FacilityGetResponse> responses = facilityService.getAll(token);
        return WebResponse.<List<FacilityGetResponse>>builder()
                .data(responses).build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<FacilityGetResponse> archive(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        FacilityGetResponse response = facilityService.archive(token, id);
        return WebResponse.<FacilityGetResponse>builder()
                .data(response).build();
    }

    @PostMapping("/{id}")
    public WebResponse<FacilityGetResponse> publish(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        FacilityGetResponse response = facilityService.publish(token, id);
        return WebResponse.<FacilityGetResponse>builder()
                .data(response).build();
    }
}
