package com.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CustomerOrder
{
    private String id;
    private String name;
    private int quantity;
    private int amount;
    private String status;
}
