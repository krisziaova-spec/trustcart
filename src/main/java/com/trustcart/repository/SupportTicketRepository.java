package com.trustcart.repository;

import com.trustcart.model.BuyerAccount;
import com.trustcart.model.Seller;
import com.trustcart.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    long countByStatus(String status);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
    List<SupportTicket> findByTypeOrderByCreatedAtDesc(String type);
    List<SupportTicket> findBySellerOrderByCreatedAtDesc(Seller seller);
    List<SupportTicket> findByBuyerOrderByCreatedAtDesc(BuyerAccount buyer);

    List<SupportTicket> findAllByTicketCodeIgnoreCaseOrderByIdAsc(String ticketCode);

    default Optional<SupportTicket> findByTicketCodeIgnoreCase(String ticketCode) {
        return findAllByTicketCodeIgnoreCaseOrderByIdAsc(ticketCode).stream().findFirst();
    }
}
