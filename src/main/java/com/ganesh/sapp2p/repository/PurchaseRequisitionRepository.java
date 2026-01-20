package com.ganesh.sapp2p.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ganesh.sapp2p.entity.PurchaseRequisition;

public interface PurchaseRequisitionRepository extends JpaRepository<PurchaseRequisition, Long> {
    Optional<PurchaseRequisition> findByPrNumber(String prNumber);
}
