package com.trustcart.repository;

import com.trustcart.model.BuyerAccount;
import com.trustcart.model.GiftRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GiftRegistryRepository extends JpaRepository<GiftRegistry, Long> {
    List<GiftRegistry> findAllByShareCodeIgnoreCaseOrderByIdAsc(String shareCode);

    default Optional<GiftRegistry> findByShareCodeIgnoreCase(String shareCode) {
        return findAllByShareCodeIgnoreCaseOrderByIdAsc(shareCode).stream().findFirst();
    }

    boolean existsByShareCodeIgnoreCase(String shareCode);
    List<GiftRegistry> findByBuyerOrderByCreatedAtDesc(BuyerAccount buyer);
    List<GiftRegistry> findByStatusOrderByCreatedAtDesc(String status);
}
