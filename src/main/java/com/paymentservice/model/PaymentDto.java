package com.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto
{
    private String id;
    private String mode;
    private String orderId;
    private int amount;
    private String status;
}
