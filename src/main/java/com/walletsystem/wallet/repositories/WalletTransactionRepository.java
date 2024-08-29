package com.walletsystem.wallet.repositories;

import com.walletsystem.wallet.models.Wallet;
import com.walletsystem.wallet.models.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {
    List<WalletTransaction> findBySenderOrReceiver(Wallet sender, Wallet receiver);
}
