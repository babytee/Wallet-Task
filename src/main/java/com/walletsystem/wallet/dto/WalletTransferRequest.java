package com.walletsystem.wallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletTransferRequest {
    private Long id;
    private Long receiverId;//from User entity
    private Long senderId;//from User entity
    private Double amount;
    private String purpose;
    private String remarks;
    private String transactionRef;
}
