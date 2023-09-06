package com.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.entity.Payment;
import com.paymentservice.event.InventoryEvent;
import com.paymentservice.event.PaymentEvent;
import com.paymentservice.exception.ResourceNotFound;
import com.paymentservice.model.CustomerOrder;
import com.paymentservice.model.Mode;
import com.paymentservice.model.PaymentDto;
import com.paymentservice.repository.PaymentRepo;
import com.paymentservice.utils.PaymentMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Log4j2
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    PaymentMapper paymentMapper;
    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, InventoryEvent> kafkaOrderTemplate;

    private static final int walletBalance = 10000;

    public Payment createPayment(Payment payment) {
        return paymentRepo.save(payment);
    }

    public void processPayment(String event) throws JsonProcessingException {
        String topicName = "order-placed";
        log.info("Recieved event {}", event);
        InventoryEvent orderEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
        PaymentEvent paymentEvent = new PaymentEvent("PAYMENT_CREATED", orderEvent.getOrderEvent());
        Payment payment = paymentMapper.toEntity(new PaymentDto(UUID.randomUUID().toString(), Mode.GPAY.name(), orderEvent.getOrderEvent().getId(), orderEvent.getOrderEvent().getAmount()
                , "SUCCESS"));
        if (orderEvent.getOrderEvent() != null && orderEvent.getOrderEvent().getAmount() > walletBalance) {
            topicName = "reversed-inventory";
            payment.setStatus("FAILED");
            paymentEvent.setStatus("PAYMENT_FAILED");
        }
        createPayment(payment);
        this.kafkaTemplate.send(topicName, paymentEvent);
    }

//    public void reversePayment(String event,Payment payment) throws JsonProcessingException
//    {
//        log.info("Entered in to the reverse paymeent");
//        PaymentEvent paymentEvent = new ObjectMapper().readValue(event, PaymentEvent.class);
//        CustomerOrder customerOrder = paymentEvent.getOrderEvent();
//
//
//        // reverse previous task
//        InventoryEvent orderEvent = new InventoryEvent("ORDER_REVERSED", paymentEvent.getOrderEvent());
//
//        //producing kafka event on topic reversed-orders
//        log.info("Producing kafka event on topic reversed-orders");
//        this.kafkaOrderTemplate.send("reversed-inventory", orderEvent);
//    }

}

