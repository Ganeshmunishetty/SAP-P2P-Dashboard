package com.ganesh.sapp2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ganesh.sapp2p.entity.PurchaseOrder;
import com.ganesh.sapp2p.entity.PurchaseRequisition;
import com.ganesh.sapp2p.repository.PurchaseOrderRepository;
import com.ganesh.sapp2p.repository.PurchaseRequisitionRepository;

@Service
public class PurchaseOrderService {

	private final PurchaseOrderRepository poRepo;
	private final PurchaseRequisitionRepository prRepo;

	public PurchaseOrderService(PurchaseOrderRepository poRepo, PurchaseRequisitionRepository prRepo) {
		this.poRepo = poRepo;
		this.prRepo = prRepo;
	}

	public PurchaseOrder createPO(PurchaseOrder po) {
		PurchaseOrder savedpo = poRepo.save(po);

		PurchaseRequisition pr = prRepo.findByPrNumber(po.getPrNumber())
				.orElseThrow(() -> new RuntimeException("PR not found"));
		pr.setStatus("APPROVED");
		prRepo.save(pr);

		return savedpo;
	}

	public List<PurchaseOrder> getAllPos() {
		return poRepo.findAll();
	}

	public PurchaseOrder getByPoNumber(String poNumber) {
		return poRepo.findByPoNumber(poNumber)
				.orElseThrow(() -> new RuntimeException(poNumber + "PO Number Not Found"));
	}

	public PurchaseOrder updatePoNumber(String poNumber, PurchaseOrder po) {
		PurchaseOrder existing = getByPoNumber(poNumber);

		existing.setNetPrice(po.getNetPrice());
		existing.setPrNumber(po.getPrNumber());
		existing.setQuantity(po.getQuantity());
		existing.setVendorName(po.getVendorName());

		return poRepo.save(existing);
	}

	public void deleteByPoNumber(String poNumber) {
		PurchaseOrder existing = poRepo.findByPoNumber(poNumber)
				.orElseThrow(() -> new RuntimeException(poNumber + "PO number not Found"));
		poRepo.delete(existing);
	}
}
