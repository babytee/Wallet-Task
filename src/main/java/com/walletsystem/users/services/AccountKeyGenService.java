package com.walletsystem.users.services;

import com.walletsystem.users.dto.AccountKeyGenResponse;
import com.walletsystem.users.repositories.AccountKeyGenRepository;
import com.walletsystem.utilities.Utility;
import com.walletsystem.wallet.models.AccountKeyGen;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service("AccountKeyGenService")
public class AccountKeyGenService {

    @Autowired
    private final AccountKeyGenRepository accountKeyGenRepository;
    private final Utility utility;

    public void createAccountKeyGen(String referenceNumber, String accountReference) {

        AccountKeyGen accountKeyGen = AccountKeyGen.builder()
                .referenceNumber(referenceNumber)
                .accountReference(accountReference)
                .build();

        accountKeyGenRepository.save(accountKeyGen);
    }

    public AccountKeyGenResponse getAccountKeyGen() {
        String referenceNumber;
        String accountReference;

        do {
            referenceNumber = Utility.generateReferenceNumber();
        } while (accountKeyGenRepository.existsByReferenceNumber(referenceNumber));

        do {
            accountReference = Utility.generateAccountReference();
        } while (accountKeyGenRepository.existsByAccountReference(accountReference));

        AccountKeyGenResponse accountKeyGen = AccountKeyGenResponse.builder()
                .referenceNumber(referenceNumber)
                .accountReference(accountReference)
                .build();

        return accountKeyGen;
    }
}

