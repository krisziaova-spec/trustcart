package com.trustcart.repository;

import com.trustcart.model.TrustCartWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrustCartWarehouseRepository extends JpaRepository<TrustCartWarehouse, Long> {
    Optional<TrustCartWarehouse> findByCodeIgnoreCase(String code);
    List<TrustCartWarehouse> findByActiveTrueOrderByCityAsc();
}
