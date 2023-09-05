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

    private static final int walletBalance = 1000;

    public Payment createPayment(Payment payment) {
        payment.setId(UUID.randomUUID().toString());
//        this.kafkaTemplate.send("payment", new PaymentEvent("Succes",new CustomerOrder("Iphone",1,10000,10001,"Varanasi")));
        return paymentRepo.save(payment);
    }

    public void processPayment(String event) throws JsonProcessingException {
        //log.info("Recieved event {}", event);

        InventoryEvent orderEvent = new ObjectMapper().readValue(event, InventoryEvent.class);
        if (orderEvent.getOrderEvent() != null && orderEvent.getOrderEvent().getAmount() <= walletBalance) {

            createPayment(paymentMapper.toEntity(new PaymentDto(UUID.randomUUID().toString(), Mode.GPAY, orderEvent.getOrderEvent().getQuantity(),
                                                  orderEvent.getOrderEvent().getAmount(), "SUCCESS")));
            //Producing kafka event on topic new-payments
            PaymentEvent paymentEvent = new PaymentEvent("PAYMENT_CREATED", orderEvent.getOrderEvent());
            log.info("Producing PaymentEvent to topic new-payments");
            this.kafkaTemplate.send("new-shipment", paymentEvent);
        } else reversePayment(event);
    }

    public void reversePayment(String event) throws JsonProcessingException {
        PaymentEvent paymentEvent = new ObjectMapper().readValue(event, PaymentEvent.class);
        CustomerOrder customerOrder = paymentEvent.getOrderEvent();
        Payment payment = this.paymentRepo.findByOrderId(customerOrder.getId()).orElseThrow(() -> new ResourceNotFound("Order Id is not found"));
        payment.setStatus("FAILED");
        createPayment(payment);

        // reverse previous task
        InventoryEvent orderEvent = new InventoryEvent("ORDER_REVERSED", paymentEvent.getOrderEvent());

        //producing kafka event on topic reversed-orders
        log.info("Producing kafka event on topic reversed-orders");
        this.kafkaOrderTemplate.send("reversed-inventory", orderEvent);
    }

}

