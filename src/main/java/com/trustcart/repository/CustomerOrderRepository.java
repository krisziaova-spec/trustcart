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
    List<CustomerOrder> findAllByOrderCodeIgnoreCaseOrderByIdAsc(String orderCode);

    default Optional<CustomerOrder> findByOrderCodeIgnoreCase(String orderCode) {
        return findAllByOrderCodeIgnoreCaseOrderByIdAsc(orderCode).stream().findFirst();
    }

    List<CustomerOrder> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    @Query("""
            select distinct o from CustomerOrder o
            left join fetch o.items i
            left join fetch i.product p
            where lower(o.email) = lower(:email)
            order by o.createdAt desc
            """)
    List<CustomerOrder> findByEmailWithItems(@Param("email") String email);
    long countByEmailIgnoreCase(String email);

    @Query("""
            select distinct o from CustomerOrder o
            join fetch o.items i
            where i.product.seller = :seller
            order by o.createdAt desc
            """)
    List<CustomerOrder> findOrdersForSeller(@Param("seller") Seller seller);

    @Query("""
            select distinct o from CustomerOrder o
            left join fetch o.items i
            order by o.createdAt desc
            """)
    List<CustomerOrder> findAllWithItems();

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
