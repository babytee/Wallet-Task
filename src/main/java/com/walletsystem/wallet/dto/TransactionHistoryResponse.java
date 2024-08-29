package com.walletsystem.wallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionHistoryResponse {
    private Long transactionId;
    private Object sender;
    private Object receiver;
    private Double amount;
    private String purpose;
    private String remarks;
    private String transactionRef;
    private LocalDateTime transactionDate;
}
