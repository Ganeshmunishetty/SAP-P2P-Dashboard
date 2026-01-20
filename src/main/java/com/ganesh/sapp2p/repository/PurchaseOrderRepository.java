package com.ganesh.sapp2p.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ganesh.sapp2p.entity.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>{

	Optional<PurchaseOrder> findByPoNumber(String poNumber); 
}
