package com.trustcart.repository;

import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Seller> findByStatusOrderByCreatedAtDesc(String status);
}
