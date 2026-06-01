package com.trustcart.repository;

import com.trustcart.model.AutoshipSubscription;
import com.trustcart.model.BuyerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutoshipSubscriptionRepository extends JpaRepository<AutoshipSubscription, Long> {
    List<AutoshipSubscription> findByBuyerOrderByCreatedAtDesc(BuyerAccount buyer);
}
