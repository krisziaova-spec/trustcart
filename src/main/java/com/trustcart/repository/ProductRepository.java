package com.trustcart.repository;

import com.trustcart.model.Product;
import com.trustcart.model.ProductCategory;
import com.trustcart.model.ProductStatus;
import com.trustcart.model.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.status = :productStatus and p.seller.status = :sellerStatus order by p.createdAt desc")
    List<Product> findLiveProducts(@Param("productStatus") ProductStatus productStatus,
                                   @Param("sellerStatus") SellerStatus sellerStatus);

    @Query("select p from Product p where p.status = :productStatus and p.seller.status = :sellerStatus and p.category = :category order by p.createdAt desc")
    List<Product> findLiveProductsByCategory(@Param("productStatus") ProductStatus productStatus,
                                             @Param("sellerStatus") SellerStatus sellerStatus,
                                             @Param("category") ProductCategory category);

    @Query("select p from Product p where p.status = :productStatus and p.seller.status = :sellerStatus and lower(p.name) like lower(concat('%', :keyword, '%')) order by p.createdAt desc")
    List<Product> searchLiveProducts(@Param("productStatus") ProductStatus productStatus,
                                     @Param("sellerStatus") SellerStatus sellerStatus,
                                     @Param("keyword") String keyword);

    Optional<Product> findByNameIgnoreCase(String name);
    List<Product> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<Product> findByStatus(ProductStatus status);
}
