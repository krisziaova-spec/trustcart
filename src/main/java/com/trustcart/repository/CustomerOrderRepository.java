package com.trustcart.repository;

import com.trustcart.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Optional<CustomerOrder> findByOrderCodeIgnoreCase(String orderCode);
    List<CustomerOrder> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email);
    long countByEmailIgnoreCase(String email);
}
