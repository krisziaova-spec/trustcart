package com.trustcart.repository;

import com.trustcart.model.GiftRegistry;
import com.trustcart.model.GiftRegistryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GiftRegistryItemRepository extends JpaRepository<GiftRegistryItem, Long> {
    List<GiftRegistryItem> findByGiftRegistryOrderByCreatedAtDesc(GiftRegistry giftRegistry);
}
