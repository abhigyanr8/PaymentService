package com.paymentservice.event;

import com.paymentservice.model.CustomerOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent
{
    private String status;
    private CustomerOrder orderEvent;
}
