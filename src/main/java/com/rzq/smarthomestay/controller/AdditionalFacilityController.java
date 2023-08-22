package com.rzq.smarthomestay.controller;

import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.service.AdditionalFacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/additionalFacilities")
public class AdditionalFacilityController {
    @Autowired
    AdditionalFacilityService additionalFacilityService;

    @PostMapping("")
    public WebResponse<AdditionalFacilityCreateResponse> create(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @RequestBody AdditionalFacilityCreateRequest request){
        AdditionalFacilityCreateResponse response = additionalFacilityService.create(token, request);
        return WebResponse.<AdditionalFacilityCreateResponse>builder()
                .data(response).build();
    }

    @PutMapping("/{id}")
    public WebResponse<AdditionalFacilityCreateResponse> update(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id, @RequestBody AdditionalFacilityCreateRequest request){
        AdditionalFacilityCreateResponse response = additionalFacilityService.update(token, id, request);
        return WebResponse.<AdditionalFacilityCreateResponse>builder()
                .data(response).build();
    }

    @GetMapping("/{id}")
    public WebResponse<AdditionalFacilityGetDetailsResponse> getById(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        AdditionalFacilityGetDetailsResponse response = additionalFacilityService.getById(token, id);
        return WebResponse.<AdditionalFacilityGetDetailsResponse>builder()
                .data(response).build();
    }

    @GetMapping("")
    public WebResponse<List<AdditionalFacilityGetResponse>> getAll(@RequestHeader(value = "X-API-TOKEN", required = false) String token){
        List<AdditionalFacilityGetResponse> responses = additionalFacilityService.getAll(token);
        return WebResponse.<List<AdditionalFacilityGetResponse>>builder()
                .data(responses).build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<AdditionalFacilityGetResponse> archive(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        AdditionalFacilityGetResponse response = additionalFacilityService.archive(token, id);
        return WebResponse.<AdditionalFacilityGetResponse>builder()
                .data(response).build();
    }

    @PostMapping("/{id}")
    public WebResponse<AdditionalFacilityGetResponse> publish(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        AdditionalFacilityGetResponse response = additionalFacilityService.publish(token, id);
        return WebResponse.<AdditionalFacilityGetResponse>builder()
                .data(response).build();
    }
}
