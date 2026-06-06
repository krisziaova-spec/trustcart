package com.trustcart.repository;

import com.trustcart.model.IncomingStockShipment;
import com.trustcart.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IncomingStockShipmentRepository extends JpaRepository<IncomingStockShipment, Long> {
    List<IncomingStockShipment> findAllByOrderByCreatedAtDesc();
    List<IncomingStockShipment> findBySellerOrderByCreatedAtDesc(Seller seller);
    List<IncomingStockShipment> findByStatusOrderByCreatedAtDesc(String status);

    List<IncomingStockShipment> findAllByShipmentCodeIgnoreCaseOrderByIdAsc(String shipmentCode);

    default Optional<IncomingStockShipment> findByShipmentCodeIgnoreCase(String shipmentCode) {
        return findAllByShipmentCodeIgnoreCaseOrderByIdAsc(shipmentCode).stream().findFirst();
    }
}
