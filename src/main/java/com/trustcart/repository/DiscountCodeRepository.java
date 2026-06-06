package com.trustcart.repository;

import com.trustcart.model.DiscountCode;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    List<DiscountCode> findAllByCodeIgnoreCaseOrderByIdAsc(String code);

    default Optional<DiscountCode> findByCodeIgnoreCase(String code) {
        return findAllByCodeIgnoreCaseOrderByIdAsc(code).stream().findFirst();
    }

    List<DiscountCode> findByActiveTrueOrderByCreatedAtDesc();
    List<DiscountCode> findBySellerOrderByCreatedAtDesc(Seller seller);
}
