package com.trustcart.repository;

import com.trustcart.model.CustomerOrder;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Optional<CustomerOrder> findByOrderCodeIgnoreCase(String orderCode);
    List<CustomerOrder> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email);
    long countByEmailIgnoreCase(String email);

    @Query("""
            select distinct o from CustomerOrder o
            join o.items i
            where i.product.seller = :seller
            order by o.createdAt desc
            """)
    List<CustomerOrder> findOrdersForSeller(@Param("seller") Seller seller);

    @Query("""
            select sum(i.lineTotal) from OrderItem i
            where i.product.seller = :seller
            """)
    BigDecimal sumSalesForSeller(@Param("seller") Seller seller);

    @Query("""
            select sum(i.quantity) from OrderItem i
            where i.product.seller = :seller
            """)
    Long sumUnitsForSeller(@Param("seller") Seller seller);
}

