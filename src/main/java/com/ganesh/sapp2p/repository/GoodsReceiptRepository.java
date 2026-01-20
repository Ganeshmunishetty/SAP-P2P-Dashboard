package com.ganesh.sapp2p.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ganesh.sapp2p.entity.GoodsReceipt;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, Long> {

	List<GoodsReceipt> findByPoNumber(String poNumber);

	Optional<GoodsReceipt> findByGrNumber(String grNumber);

//	boolean existsByGrNumber(String grNumber);

}
