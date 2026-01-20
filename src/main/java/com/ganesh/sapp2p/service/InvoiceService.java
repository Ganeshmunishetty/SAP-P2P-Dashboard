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
	            .orElseThrow(() -> new RuntimeException("PO not Found"));

	    List<GoodsReceipt> grList = grRepo.findByPoNumber(invoice.getPoNumber());

	    if (grList.isEmpty()) {
	        invoice.setInvoiceStatus("BLOCKED");
	        invoice.setInvoiceAmount(0);
	        return irRepo.save(invoice);
	    }

	    // Total quantity received from all GRs
	    int totalReceivedQty = grList.stream().mapToInt(GoodsReceipt::getReceivedQuantity).sum();

	    // Unit price per item
	    double unitPrice = po.getNetPrice() / po.getQuantity();

	    // Calculate invoice amount based on received quantity
	    double calculatedInvoiceAmount = totalReceivedQty * unitPrice;
	    invoice.setInvoiceAmount(calculatedInvoiceAmount);

	    // Auto-generate invoice number per PO
	    if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
	        int irCount = irRepo.findByPoNumber(invoice.getPoNumber()).size() + 1;
	        invoice.setInvoiceNumber(invoice.getPoNumber() + "-IR" + irCount);
	    }

	    // Total invoiced amount including this invoice
	    double totalInvoiceAmount = irRepo.findByPoNumber(invoice.getPoNumber())
	                                      .stream()
	                                      .mapToDouble(Invoice::getInvoiceAmount)
	                                      .sum() + calculatedInvoiceAmount;

	    // Use a small tolerance for floating-point comparison
	    double tolerance = 0.01;

	    if (totalReceivedQty == po.getQuantity() &&
	        Math.abs(totalInvoiceAmount - po.getNetPrice()) < tolerance) {

	        invoice.setInvoiceStatus("MATCHED");

	        // Update all previous invoices for this PO
	        irRepo.findByPoNumber(invoice.getPoNumber()).forEach(inv -> {
	            inv.setInvoiceStatus("MATCHED");
	            irRepo.save(inv);
	        });
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
