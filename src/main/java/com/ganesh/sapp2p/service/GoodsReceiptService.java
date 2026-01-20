package com.ganesh.sapp2p.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.ganesh.sapp2p.entity.PurchaseOrder;
import com.ganesh.sapp2p.entity.PurchaseRequisition;
import com.ganesh.sapp2p.entity.GoodsReceipt;
import com.ganesh.sapp2p.entity.Invoice;
import com.ganesh.sapp2p.repository.GoodsReceiptRepository;
import com.ganesh.sapp2p.repository.InvoiceReceiptRepository;
import com.ganesh.sapp2p.repository.PurchaseOrderRepository;
import com.ganesh.sapp2p.repository.PurchaseRequisitionRepository;

@Service
public class GoodsReceiptService {

	private final GoodsReceiptRepository grRepo;
	private final PurchaseOrderRepository poRepo;
	private final PurchaseRequisitionRepository prRepo;
	private final InvoiceReceiptRepository irRepo;

	public GoodsReceiptService(GoodsReceiptRepository grRepo, PurchaseOrderRepository poRepo,
			PurchaseRequisitionRepository prRepo, InvoiceReceiptRepository irRepo) {
		this.grRepo = grRepo;
		this.poRepo = poRepo;
		this.prRepo = prRepo;
		this.irRepo = irRepo;
	}

	public GoodsReceipt createGR(GoodsReceipt gr) {
		PurchaseOrder po = poRepo.findByPoNumber(gr.getPoNumber())
				.orElseThrow(() -> new RuntimeException("PO number Not Found " + gr.getPoNumber()));

		int TotalReceived = grRepo.findByPoNumber(gr.getPoNumber()).stream().mapToInt(GoodsReceipt::getReceivedQuantity)
				.sum();

		if (TotalReceived + gr.getReceivedQuantity() > po.getQuantity()) {
			throw new RuntimeException("GR Quantity Exceeds PO Quantity");
		}

		if (TotalReceived + gr.getReceivedQuantity() == po.getQuantity()) {
			gr.setGrStatus("RECEIVED");
		} else {
			gr.setGrStatus("PARTIAL");
		}

		if (gr.getGrNumber() == null || gr.getGrNumber().isEmpty()) {
			int nextGrCount = grRepo.findByPoNumber(gr.getPoNumber()).size() + 1;
			gr.setGrNumber(gr.getPoNumber() + "-GR" + nextGrCount);
		}
		GoodsReceipt savedGr = grRepo.save(gr);
		if (gr.getGrStatus().equals("Received")) {
			PurchaseRequisition pr = prRepo.findByPrNumber(po.getPrNumber())
					.orElseThrow(() -> new RuntimeException("PR not found for Po : " + po.getPoNumber()));
			pr.setStatus("Completed");
			prRepo.save(pr);
		}
		List<Invoice> invoices = irRepo.findByPoNumber(po.getPoNumber());
		int totalGRQty = grRepo.findByPoNumber(po.getPoNumber()).stream().mapToInt(GoodsReceipt::getReceivedQuantity)
				.sum();

		double unitPrice = po.getNetPrice() / po.getQuantity();
		double totalInvoiceAmount = totalGRQty * unitPrice;

		invoices.forEach(inv -> {
			inv.setInvoiceAmount(unitPrice * grRepo.findByPoNumber(po.getPoNumber()).stream()
					.filter(g -> g.getGrNumber().equals(inv.getGrNumber())).mapToInt(GoodsReceipt::getReceivedQuantity)
					.sum());

			if (totalGRQty == po.getQuantity()) {
				inv.setInvoiceStatus("MATCHED");
			} else {
				inv.setInvoiceStatus("BLOCKED");
			}
			irRepo.save(inv);
		});
		return grRepo.save(gr);
	}

	public List<GoodsReceipt> getAllGRs() {
		return grRepo.findAll();
	}

	public GoodsReceipt getByGrNumber(String grNumber) {
		return grRepo.findByGrNumber(grNumber)
				.orElseThrow(() -> new RuntimeException(" GR Number Not Found" + grNumber));
	}

	public GoodsReceipt updateByGrNumber(GoodsReceipt gr, String grNumber) {
		GoodsReceipt existing = getByGrNumber(grNumber);

		PurchaseOrder po = poRepo.findByPoNumber(existing.getPoNumber())
				.orElseThrow(() -> new RuntimeException("PO not Found"));

		int otherReceivedQuantity = grRepo.findByPoNumber(existing.getPoNumber()).stream()
				.filter(g -> !g.getGrNumber().equals(grNumber)).mapToInt(GoodsReceipt::getReceivedQuantity).sum();

		if (otherReceivedQuantity + gr.getReceivedQuantity() > po.getQuantity()) {
			throw new RuntimeException("Updated quantity exceeds PO quantity");
		}

		existing.setReceivedQuantity(gr.getReceivedQuantity());

		if (otherReceivedQuantity + gr.getReceivedQuantity() == po.getQuantity()) {

			existing.setGrStatus("RECEIVED");
			PurchaseRequisition pr = prRepo.findByPrNumber(po.getPrNumber())
					.orElseThrow(() -> new RuntimeException("PR not Found"));
			pr.setStatus("COMPLETED");
			prRepo.save(pr);
		} else {
			existing.setGrStatus("PARTIAL");
		}

		return grRepo.save(existing);
	}

	public void deleteByGrNumber(String grNumber) {
		GoodsReceipt existing = grRepo.findByGrNumber(grNumber).orElseThrow(() -> new RuntimeException("GR Not Found"));
		grRepo.delete(existing);
	}
}
