package com.fpt.StreamGAP.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String orderId;
    private String totalPrice;
    private String paymentTime;
    private String transactionId;
}


