package com.paymentservice.controller;

import com.paymentservice.entity.Payment;
import com.paymentservice.service.PaymentServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.UUID;


@Log4j2
@RestController
@RequestMapping("payment")

public class PaymentController {
    @Autowired
    private PaymentServiceImpl paymentService;

    @KafkaListener(topics = "new-payments",groupId = "payments-group")
    public void processPayment(String event) throws JsonProcessingException
    {
        System.out.println("Recevied event "+event);
        log.info("Received event {}", event);
        paymentService.processPayment(event);

    }

    @KafkaListener(topics = "reverse-payment", groupId = "payments-group")
    public void reversePayment(String event) throws JsonProcessingException {
        log.info("Reverse Payment {}", event);
        paymentService.reversePayment(event);
    }


}

