package com.paymentservice.utils;

import com.paymentservice.entity.Payment;
import com.paymentservice.model.PaymentDto;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper implements Mapper<PaymentDto, Payment> {
    @Override
    public Payment toEntity(PaymentDto dto) {
        return new Payment(dto.getId(), dto.getMode(), dto.getOrderId(), dto.getAmount(), dto.getStatus());
    }

    @Override
    public PaymentDto toDto(Payment e) {
        return new PaymentDto(e.getId(), e.getMode(), e.getOrderId(), e.getAmount(), e.getStatus());
    }
}
