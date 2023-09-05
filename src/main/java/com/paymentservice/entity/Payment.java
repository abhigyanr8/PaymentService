package com.paymentservice.entity;

import com.paymentservice.model.Mode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Payment
{
    @Id
    private String id;
    private Mode mode;
    private int orderId;
    private int amount;
    private String status;
}
