package com.trustcart.repository;

import com.trustcart.model.Product;
import com.trustcart.model.ProductCategory;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatusOrderByCreatedAtDesc(String status);
    List<Product> findByCategoryAndStatusOrderByCreatedAtDesc(ProductCategory category, String status);
    List<Product> findByNameContainingIgnoreCaseAndStatusOrderByCreatedAtDesc(String name, String status);
    List<Product> findBySellerOrderByCreatedAtDesc(Seller seller);
    List<Product> findByTryOnEligibleTrueAndStatusOrderByNameAsc(String status);
    Optional<Product> findByNameIgnoreCase(String name);
}
