package com.trustcart.repository;

import com.trustcart.model.RefundRequest;
import com.trustcart.model.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findTop20ByOrderByCreatedAtDesc();
    List<RefundRequest> findByStatus(RefundStatus status);
}
