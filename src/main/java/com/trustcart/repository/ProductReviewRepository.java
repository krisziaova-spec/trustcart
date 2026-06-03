package com.trustcart.repository;

import com.trustcart.model.BuyerAccount;
import com.trustcart.model.Product;
import com.trustcart.model.ProductReview;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProductAndStatusOrderByCreatedAtDesc(Product product, String status);
    List<ProductReview> findByProductOrderByCreatedAtDesc(Product product);
    List<ProductReview> findByBuyerOrderByCreatedAtDesc(BuyerAccount buyer);
    List<ProductReview> findBySellerOrderByCreatedAtDesc(Seller seller);
}
