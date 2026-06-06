package com.trustcart.repository;

import com.trustcart.model.BuyerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BuyerAccountRepository extends JpaRepository<BuyerAccount, Long> {
    List<BuyerAccount> findAllByEmailIgnoreCaseOrderByIdAsc(String email);

    default Optional<BuyerAccount> findByEmailIgnoreCase(String email) {
        return findAllByEmailIgnoreCaseOrderByIdAsc(email).stream().findFirst();
    }

    boolean existsByEmailIgnoreCase(String email);
}
