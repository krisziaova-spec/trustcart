package com.trustcart.repository;

import com.trustcart.model.BuyerAccount;
import com.trustcart.model.ChatThread;
import com.trustcart.model.Product;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChatThreadRepository extends JpaRepository<ChatThread, Long> {
    List<ChatThread> findByBuyerOrderByUpdatedAtDesc(BuyerAccount buyer);
    List<ChatThread> findBySellerOrderByUpdatedAtDesc(Seller seller);
    Optional<ChatThread> findByBuyerAndSellerAndProduct(BuyerAccount buyer, Seller seller, Product product);
}
