package com.trustcart.repository;

import com.trustcart.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCodeIgnoreCase(String code);
    List<DiscountCode> findTop20ByOrderByCreatedAtDesc();
    List<DiscountCode> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
}
