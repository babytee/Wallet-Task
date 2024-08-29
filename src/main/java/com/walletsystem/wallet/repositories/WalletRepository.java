package com.walletsystem.wallet.repositories;

import com.walletsystem.users.models.User;
import com.walletsystem.wallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Optional<Wallet>findByUser(User user);
}
