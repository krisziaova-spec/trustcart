package com.trustcart.repository;

import com.trustcart.model.DiscountCode;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCodeIgnoreCase(String code);
    List<DiscountCode> findByActiveTrueOrderByCreatedAtDesc();
    List<DiscountCode> findBySellerOrderByCreatedAtDesc(Seller seller);
}
