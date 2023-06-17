package com.rzq.smarthomestay.service;

import com.rzq.smarthomestay.model.TransactionGetDetailsResponse;
import com.rzq.smarthomestay.model.TransactionGetResponse;
import com.rzq.smarthomestay.model.TransactionOrderRequest;
import com.rzq.smarthomestay.model.TransactionOrderResponse;

import java.util.List;

public interface TransactionService {
    public TransactionGetDetailsResponse getById(String token, String id);
    public List<TransactionGetResponse> getMyTransaction(String token);
    public List<TransactionGetResponse> getAllTransaction(String token);
    public TransactionOrderResponse order(String token, TransactionOrderRequest request);
    public TransactionOrderResponse approval(String token, String id, String action);
}
