package com.ganesh.sapp2p.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ganesh.sapp2p.entity.GoodsReceipt;
import com.ganesh.sapp2p.entity.Invoice;

public interface InvoiceReceiptRepository extends JpaRepository<Invoice, Long> {

	Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

	List<Invoice> findByPoNumber(String poNumber);
	
	List<GoodsReceipt> findByGrNumber(String grNumber);

}
