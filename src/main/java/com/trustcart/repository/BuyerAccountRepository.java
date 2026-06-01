package com.trustcart.repository;

import com.trustcart.model.BuyerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BuyerAccountRepository extends JpaRepository<BuyerAccount, Long> {
    Optional<BuyerAccount> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
