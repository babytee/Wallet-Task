package com.walletsystem.wallet.services;

import com.walletsystem.common.StandardResponse;
import com.walletsystem.utilities.UserInfoService;
import com.walletsystem.wallet.dto.BankDetailRequest;
import com.walletsystem.wallet.dto.TransactionHistoryResponse;
import com.walletsystem.wallet.dto.WalletResponse;
import com.walletsystem.wallet.dto.WalletTransferRequest;
import com.walletsystem.wallet.models.BankDetail;
import com.walletsystem.wallet.models.Wallet;
import com.walletsystem.wallet.models.WalletTransaction;
import com.walletsystem.wallet.repositories.BankDetailRepository;
import com.walletsystem.wallet.repositories.WalletRepository;
import com.walletsystem.wallet.repositories.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("UserWalletService")
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserInfoService userInfoService;
    private final WalletTransactionRepository walletTransactionRepository;
    private final BankDetailRepository bankDetailRepository;

    public ResponseEntity<?> getWalletBalance() {
        var user = userInfoService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.internalServerError().
                    body(StandardResponse.error("User not authenticated"));
        }

        Optional<Wallet> optionalWallet = walletRepository.findByUser(user);
        Wallet wallet = optionalWallet.get();
        WalletResponse walletResponse = WalletResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .userDetails(userInfoService.getAuthProfile())
                .build();

        return ResponseEntity.ok(StandardResponse.success(walletResponse));

    }

    public ResponseEntity<?> transfer(WalletTransferRequest request) {
        var user = userInfoService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.internalServerError().
                    body(StandardResponse.error("User not authenticated"));
        }
        var receiverInfo = userInfoService.getUserById(request.getReceiverId());
        var senderInfo = userInfoService.getUserById(request.getSenderId());
        if (receiverInfo == null) {
            return ResponseEntity.ok(StandardResponse.error("receiverId can not be null"));
        } else if (senderInfo == null) {
            return ResponseEntity.ok(StandardResponse.error("senderId can not be null"));
        }

        Wallet sender = walletRepository.findByUser(senderInfo)
                .orElse(null);
        Wallet receiver = walletRepository.findByUser(receiverInfo)
                .orElse(null);

        if (sender == null) {
            return ResponseEntity.ok(StandardResponse.error("Sender wallet not found"));
        } else if (receiver == null) {
            return ResponseEntity.ok(StandardResponse.error("Receiver wallet not found"));
        }

        if (sender.getBalance() < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - request.getAmount());
        receiver.setBalance(receiver.getBalance() + request.getAmount());

        walletRepository.save(sender);
        walletRepository.save(receiver);

        WalletTransaction transaction = WalletTransaction.builder()
                .sender(sender)
                .receiver(receiver)
                .amount(request.getAmount())
                .transactionDate(LocalDateTime.now())
                .build();

        walletTransactionRepository.save(transaction);

        return ResponseEntity.ok(StandardResponse.success("Wallet To Wallet Transfer Successful"));
    }

    public ResponseEntity<?> getTransactionHistory() {

        var user = userInfoService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.internalServerError().
                    body(StandardResponse.error("User not authenticated"));
        }
        Wallet wallet = walletRepository.findByUser(user)
                .orElse(null);
        List<WalletTransaction> walletTransactions =
                walletTransactionRepository.findBySenderOrReceiver(wallet, wallet);

        List<TransactionHistoryResponse> historyResponses =
                walletTransactions.stream().map(this::mapToTransactionHistory)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(StandardResponse.success(historyResponses));
    }

    private TransactionHistoryResponse mapToTransactionHistory(WalletTransaction transaction) {
        var sender = userInfoService.mapToUserResponse
                (transaction.getSender().getUser());

        var receiver = userInfoService.mapToUserResponse
                (transaction.getReceiver().getUser());

        return TransactionHistoryResponse.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .sender(sender)
                .receiver(receiver)
                .purpose(transaction.getPurpose())
                .remarks(transaction.getRemarks())
                .transactionRef(transaction.getTransactionRef())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

    public ResponseEntity<?> getBankDetails() {
        var user = userInfoService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.internalServerError().
                    body(StandardResponse.error("User not authenticated"));
        }

        Optional<BankDetail> optionalBankDetail = bankDetailRepository.findByUser(user);
        String fname = userInfoService.getAuthProfile().getFirstName();
        String lname = userInfoService.getAuthProfile().getLastName();
        String fullName = lname + " " + fname;

        BankDetail bankDetail = optionalBankDetail.get();
        BankDetailRequest bankDetailRequest = BankDetailRequest.builder()
                .id(bankDetail.getId())
                .accountNumber(bankDetail.getAccountNumber())
                .accountName(fullName)
                .userDetails(userInfoService.getAuthProfile())
                .build();

        return ResponseEntity.ok(StandardResponse.success(bankDetailRequest));
    }
}
