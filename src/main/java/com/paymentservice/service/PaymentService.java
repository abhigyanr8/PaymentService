package com.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface PaymentService {

    void processPayment(String event) throws JsonProcessingException;

//    void reversePayment(String event) throws JsonProcessingException;

}
