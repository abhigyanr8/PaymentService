package com.paymentservice.event;

import com.paymentservice.model.CustomerOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent
{
    private String status;
    private CustomerOrder orderEvent;
}



