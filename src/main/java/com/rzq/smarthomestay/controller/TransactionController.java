package com.rzq.smarthomestay.controller;

import com.rzq.smarthomestay.model.*;
import com.rzq.smarthomestay.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping("/{id}")
    public WebResponse<TransactionGetDetailsResponse> getById(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id){
        TransactionGetDetailsResponse response = transactionService.getById(token, id);
        return WebResponse.<TransactionGetDetailsResponse>builder()
                .data(response).build();
    }

    @GetMapping("/myTransaction")
    public WebResponse<List<TransactionGetResponse>> myTransaction(@RequestHeader(value = "X-API-TOKEN", required = false) String token){
        List<TransactionGetResponse> responses = transactionService.getMyTransaction(token);
        return WebResponse.<List<TransactionGetResponse>>builder()
                .data(responses).build();
    }
    @GetMapping("/allTransaction")
    public WebResponse<List<TransactionGetResponse>> allTransaction(@RequestHeader(value = "X-API-TOKEN", required = false) String token){
        List<TransactionGetResponse> responses = transactionService.getAllTransaction(token);
        return WebResponse.<List<TransactionGetResponse>>builder()
                .data(responses).build();
    }

    @PostMapping("")
    public WebResponse<TransactionOrderResponse> order(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @RequestBody TransactionOrderRequest request){
        TransactionOrderResponse response = transactionService.order(token, request);
        return WebResponse.<TransactionOrderResponse>builder()
                .data(response).build();
    }

    @PutMapping("/{id}")
    public WebResponse<TransactionOrderResponse> approval(@RequestHeader(value = "X-API-TOKEN", required = false) String token, @PathVariable("id") String id, @RequestParam(value = "action", required = true) String action){
        TransactionOrderResponse response = transactionService.approval(token, id, action);
        return WebResponse.<TransactionOrderResponse>builder()
                .data(response).build();
    }
}
