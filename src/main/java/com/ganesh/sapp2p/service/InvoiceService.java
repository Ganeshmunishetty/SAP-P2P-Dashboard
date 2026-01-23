package com.ganesh.sapp2p.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ganesh.sapp2p.entity.GoodsReceipt;
import com.ganesh.sapp2p.entity.Invoice;
import com.ganesh.sapp2p.entity.PurchaseOrder;
import com.ganesh.sapp2p.repository.GoodsReceiptRepository;
import com.ganesh.sapp2p.repository.InvoiceReceiptRepository;
import com.ganesh.sapp2p.repository.PurchaseOrderRepository;
import com.ganesh.sapp2p.repository.PurchaseRequisitionRepository;

@Service
public class InvoiceService {
	private final InvoiceReceiptRepository irRepo;
	private final PurchaseOrderRepository poRepo;
	private final GoodsReceiptRepository grRepo;

	public InvoiceService(InvoiceReceiptRepository irRepo, PurchaseOrderRepository poRepo,
			GoodsReceiptRepository grRepo) {
		this.irRepo = irRepo;
		this.poRepo = poRepo;
		this.grRepo = grRepo;
	}

	public Invoice createIR(Invoice invoice) {
	    PurchaseOrder po = poRepo.findByPoNumber(invoice.getPoNumber())
	        .orElseThrow(() -> new RuntimeException("PO not Found: " + invoice.getPoNumber()));

	    // Auto-generate IR number
	    if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
	        int nextIR = irRepo.findByPoNumber(po.getPoNumber()).size() + 1;
	        invoice.setInvoiceNumber(po.getPoNumber() + "-IR" + nextIR);
	    }

	    // Calculate invoice amount based on total GR received
	    List<GoodsReceipt> grList = grRepo.findByPoNumber(po.getPoNumber());
	    int totalGRQty = grList.stream().mapToInt(GoodsReceipt::getReceivedQuantity).sum();
	    double unitPrice = po.getNetPrice() / po.getQuantity();
	    double calculatedAmount = totalGRQty * unitPrice;
	    invoice.setInvoiceAmount(calculatedAmount);

	    // Determine initial status for the new IR
	    if (totalGRQty == po.getQuantity() && calculatedAmount == po.getNetPrice()) {
	        invoice.setInvoiceStatus("MATCHED");

	        //  Update all previous IRs for the same PO to MATCHED
	        List<Invoice> oldIRs = irRepo.findByPoNumber(invoice.getPoNumber());
	        for (Invoice old : oldIRs) {
	            old.setInvoiceStatus("MATCHED");
	            irRepo.save(old);
	        }

	    } else {
	        invoice.setInvoiceStatus("BLOCKED");
	    }

	    return irRepo.save(invoice);
	}


	public List<Invoice> getAllIRs() {
		return irRepo.findAll();
	}

	public Invoice getByInvoiceNumber(String invoiceNumber) {
		return irRepo.findByInvoiceNumber(invoiceNumber)
				.orElseThrow(() -> new RuntimeException("Invoice Number Not Found"));
	}

	public Invoice updateByIRNumber(Invoice invoice, String invoiceNumber) {
		Invoice existing = irRepo.findByInvoiceNumber(invoiceNumber)
				.orElseThrow(() -> new RuntimeException("Invoice not Found"));

		PurchaseOrder po = poRepo.findByPoNumber(existing.getPoNumber())
				.orElseThrow(() -> new RuntimeException("PO not Found"));

		int totalReceivedQty = grRepo.findByPoNumber(existing.getPoNumber()).stream()
				.mapToInt(GoodsReceipt::getReceivedQuantity).sum();
		existing.setInvoiceAmount(invoice.getInvoiceAmount());

		double expectedAmount = (totalReceivedQty * po.getNetPrice()) / po.getQuantity();
		if (existing.getInvoiceAmount() == expectedAmount) {
			existing.setInvoiceStatus("MATCHED");
		} else {
			existing.setInvoiceStatus("BLOCKED");
		}
		return irRepo.save(existing);
	}

	public void deleteByIRnumber(String invoiceNumber) {
		Invoice inv = irRepo.findByInvoiceNumber(invoiceNumber).orElseThrow(() -> new RuntimeException("IR Not Found"));
		irRepo.delete(inv);
	}
}
