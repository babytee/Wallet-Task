package com.walletsystem.wallet.repositories;

import com.walletsystem.users.models.User;
import com.walletsystem.wallet.models.BankDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankDetailRepository extends JpaRepository<BankDetail, Long> {
    Optional<BankDetail>findByUser(User user);
}
