package com.ganesh.sapp2p.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.ganesh.sapp2p.entity.PurchaseOrder;
import com.ganesh.sapp2p.repository.PurchaseOrderRepository;
import com.ganesh.sapp2p.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/po")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;

    public PurchaseOrderController(PurchaseOrderService poService) {
        this.poService = poService;
    }

    @PostMapping
    public PurchaseOrder createPO(@RequestBody PurchaseOrder po) {
        return poService.createPO(po);
    }

    @GetMapping
    public List<PurchaseOrder> getAllPOs() {
        return poService.getAllPos();
    }
    @GetMapping("/{poNumber}")
    public PurchaseOrder getByPoNumber(@PathVariable String poNumber) {
    	return poService.getByPoNumber(poNumber);
    }
    @PutMapping("/{poNumber}")
    public PurchaseOrder updatePoNumber(@RequestBody PurchaseOrder po,@PathVariable String poNumber ) {
    	return poService.updatePoNumber(poNumber, po);
    }
    @DeleteMapping("/{poNumber}")
    public String deleteByPoNumber(@PathVariable String poNumber) {
    	 poService.deleteByPoNumber(poNumber);
    	 return "PO"+poNumber+"SuccessfullyDeleted";
    }
}
