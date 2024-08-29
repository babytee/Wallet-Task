package com.walletsystem.wallet.controllers;

import com.walletsystem.wallet.dto.WalletTransferRequest;
import com.walletsystem.wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController("UserWalletController")
@RequestMapping({"/api/v1/user/wallet"})
public class WalletController {
    private final WalletService walletService;

    @GetMapping("get_balance")
    public ResponseEntity<?> getWalletBalance() {
        return walletService.getWalletBalance();
    }

    @PostMapping("wallet_transfer")
    public ResponseEntity<?> transferWallet(@RequestBody WalletTransferRequest request) {
        return walletService.transfer(request);
    }

    @GetMapping("transaction_histories")
    public ResponseEntity<?> transactionHistories() {
        return walletService.getTransactionHistory();
    }


    @GetMapping("get_bank_details")
    public ResponseEntity<?> getBankDetails() {
        return walletService.getBankDetails();
    }
}
