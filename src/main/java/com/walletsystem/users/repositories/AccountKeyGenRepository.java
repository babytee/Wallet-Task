package com.walletsystem.users.repositories;

import com.walletsystem.wallet.models.AccountKeyGen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountKeyGenRepository extends JpaRepository<AccountKeyGen, Long> {
        boolean existsByReferenceNumber(String referenceNumber);
        boolean existsByAccountReference(String accountReference);

}
