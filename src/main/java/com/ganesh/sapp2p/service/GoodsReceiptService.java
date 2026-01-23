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
                .orElseThrow(() -> new RuntimeException("PO Not Found: " + gr.getPoNumber()));

        // Prevent GR creation if PO is completed
        if ("GR_COMPLETED".equals(po.getPoStatus())) {
            throw new RuntimeException("GR not allowed. PO already Completed.");
        }

        // Calculate total received quantity
        int totalReceived = grRepo.findByPoNumber(gr.getPoNumber())
                .stream()
                .mapToInt(GoodsReceipt::getReceivedQuantity)
                .sum();

        if (totalReceived + gr.getReceivedQuantity() > po.getQuantity()) {
            throw new RuntimeException("GR Quantity Exceeds PO Quantity");
        }

        // Set GR status
        if (totalReceived + gr.getReceivedQuantity() == po.getQuantity()) {
            gr.setGrStatus("RECEIVED");
            po.setPoStatus("GR_COMPLETED");
            poRepo.save(po);

            // ✅ Once final GR is received, update all previous GRs
            List<GoodsReceipt> allGRs = grRepo.findByPoNumber(po.getPoNumber());
            for (GoodsReceipt g : allGRs) {
                g.setGrStatus("RECEIVED");
                grRepo.save(g);
            }

            // Update all invoices for the PO to COMPLETED
            List<Invoice> invoices = irRepo.findByPoNumber(po.getPoNumber());
            for (Invoice inv : invoices) {
                inv.setInvoiceStatus("COMPLETED");
                irRepo.save(inv);
            }

            // Update PR
            PurchaseRequisition pr = prRepo.findByPrNumber(po.getPrNumber())
                    .orElseThrow(() -> new RuntimeException("PR not found for PO: " + po.getPoNumber()));
            pr.setStatus("COMPLETED");
            prRepo.save(pr);

        } else {
            gr.setGrStatus("PARTIAL");
        }

        // Auto-generate GR number
        if (gr.getGrNumber() == null || gr.getGrNumber().isEmpty()) {
            int nextGrCount = grRepo.findByPoNumber(gr.getPoNumber()).size() + 1;
            gr.setGrNumber(po.getPoNumber() + "-GR" + nextGrCount);
        }

        return grRepo.save(gr);
    }

    public List<GoodsReceipt> getAllGRs() {
        return grRepo.findAll();
    }

    public GoodsReceipt getByGrNumber(String grNumber) {
        return grRepo.findByGrNumber(grNumber)
                .orElseThrow(() -> new RuntimeException(" GR Number Not Found: " + grNumber));
    }

    public GoodsReceipt updateByGrNumber(GoodsReceipt gr, String grNumber) {
        GoodsReceipt existing = getByGrNumber(grNumber);

        PurchaseOrder po = poRepo.findByPoNumber(existing.getPoNumber())
                .orElseThrow(() -> new RuntimeException("PO not Found"));

        int otherReceivedQuantity = grRepo.findByPoNumber(existing.getPoNumber())
                .stream()
                .filter(g -> !g.getGrNumber().equals(grNumber))
                .mapToInt(GoodsReceipt::getReceivedQuantity)
                .sum();

        if (otherReceivedQuantity + gr.getReceivedQuantity() > po.getQuantity()) {
            throw new RuntimeException("Updated quantity exceeds PO quantity");
        }

        existing.setReceivedQuantity(gr.getReceivedQuantity());

        if (otherReceivedQuantity + gr.getReceivedQuantity() == po.getQuantity()) {
            existing.setGrStatus("RECEIVED");

            // ✅ Once final GR is received, update all previous GRs
            List<GoodsReceipt> allGRs = grRepo.findByPoNumber(po.getPoNumber());
            for (GoodsReceipt g : allGRs) {
                g.setGrStatus("RECEIVED");
                grRepo.save(g);
            }

            // Update PR
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
        GoodsReceipt existing = grRepo.findByGrNumber(grNumber)
                .orElseThrow(() -> new RuntimeException("GR Not Found"));
        grRepo.delete(existing);
    }
}
