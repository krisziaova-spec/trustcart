package com.trustcart.repository;

import com.trustcart.model.AutoshipSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoshipSubscriptionRepository extends JpaRepository<AutoshipSubscription, Long> {
    List<AutoshipSubscription> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    List<AutoshipSubscription> findTop20ByOrderByCreatedAtDesc();
}
