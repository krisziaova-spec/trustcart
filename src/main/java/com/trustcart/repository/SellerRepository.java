package com.trustcart.repository;

import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    List<Seller> findAllByEmailIgnoreCaseOrderByIdAsc(String email);

    default Optional<Seller> findByEmailIgnoreCase(String email) {
        return findAllByEmailIgnoreCaseOrderByIdAsc(email).stream().findFirst();
    }

    boolean existsByEmailIgnoreCase(String email);
    List<Seller> findByStatusOrderByCreatedAtDesc(String status);
}
