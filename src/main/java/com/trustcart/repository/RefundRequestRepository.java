package com.trustcart.repository;

import com.trustcart.model.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
