package com.ganesh.sapp2p.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ganesh.sapp2p.entity.GoodsReceipt;
import com.ganesh.sapp2p.entity.Invoice;
import com.ganesh.sapp2p.entity.PurchaseOrder;
import com.ganesh.sapp2p.repository.GoodsReceiptRepository;
import com.ganesh.sapp2p.repository.InvoiceReceiptRepository;
import com.ganesh.sapp2p.repository.PurchaseOrderRepository;

@Service
public class ThreeWayMatchService {

	private final PurchaseOrderRepository poRepo;
	private final GoodsReceiptRepository grRepo;
	private final InvoiceReceiptRepository invRepo;

	public ThreeWayMatchService(PurchaseOrderRepository poRepo, GoodsReceiptRepository grRepo,
			InvoiceReceiptRepository invRepo) {
		this.poRepo = poRepo;
		this.grRepo = grRepo;
		this.invRepo = invRepo;

	}

	public Invoice performThreeWayMatching(String irNumber) {

		Invoice inv = invRepo.findByInvoiceNumber(irNumber)
				.orElseThrow(() -> new RuntimeException("Invoice Not Found"));

		PurchaseOrder po = poRepo.findByPoNumber(inv.getPoNumber())
				.orElseThrow(() -> new RuntimeException("Purchase Order Not Found"));
		
		int totalReceivedQty=grRepo.findByPoNumber(po.getPoNumber())
				.stream()
				.mapToInt(GoodsReceipt::getReceivedQuantity)
				.sum();
		double expectedAmount=(totalReceivedQty*po.getNetPrice())/po.getQuantity();
		if(inv.getInvoiceAmount()==expectedAmount&& totalReceivedQty<=po.getNetPrice()) {
			inv.setInvoiceStatus("Matched");
		}else {
			inv.setInvoiceStatus("Blocked");
		}
		return invRepo.save(inv);
	}
}
