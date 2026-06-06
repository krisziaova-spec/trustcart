package com.trustcart.repository;

import com.trustcart.model.Product;
import com.trustcart.model.ProductCategory;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusOrderByCreatedAtDesc(String status);
    List<Product> findByCategoryAndStatusOrderByCreatedAtDesc(ProductCategory category, String status);
    List<Product> findByNameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(String name, String status);

    @Query("""
            select p from Product p
            join p.seller s
            where p.status = :status
              and (
                lower(p.name) like lower(concat('%', :term, '%'))
                or lower(coalesce(p.description, '')) like lower(concat('%', :term, '%'))
                or lower(coalesce(p.sustainabilityTag, '')) like lower(concat('%', :term, '%'))
                or lower(coalesce(p.photoAltText, '')) like lower(concat('%', :term, '%'))
                or lower(coalesce(p.productOrigin, '')) like lower(concat('%', :term, '%'))
                or lower(coalesce(s.storeName, '')) like lower(concat('%', :term, '%'))
                or lower(coalesce(s.storeCity, '')) like lower(concat('%', :term, '%'))
                or lower(coalesce(s.storeProvince, '')) like lower(concat('%', :term, '%'))
              )
            order by p.createdAt desc
            """)
    List<Product> searchApprovedProducts(@Param("term") String term, @Param("status") String status);
    List<Product> findBySellerOrderByCreatedAtDesc(Seller seller);
    List<Product> findBySellerAndStatusOrderByCreatedAtDesc(Seller seller, String status);
    List<Product> findBySubscriptionEligibleTrueAndStatusOrderByCreatedAtDesc(String status);
    List<Product> findByTryOnEligibleTrueAndStatusOrderByNameAsc(String status);
    Optional<Product> findByNameIgnoreCase(String name);
}
