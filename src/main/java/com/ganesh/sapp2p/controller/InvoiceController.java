package com.ganesh.sapp2p.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganesh.sapp2p.entity.Invoice;
import com.ganesh.sapp2p.service.InvoiceService;

import org.springframework.web.bind.annotation.RequestBody;
@RestController
@RequestMapping("api/invoice") // ✔ Base URL
public class InvoiceController {

    private final InvoiceService irService;

    public InvoiceController(InvoiceService irService) {
        this.irService = irService; // ✔ Constructor injection (best practice)
    }

    // ================= CREATE INVOICE =================
    @PostMapping
    public Invoice createInvoice(@RequestBody Invoice inv) {
        // ✔ Accepts Invoice
        // ✔ Returns Invoice
        // ✔ Matches service return type
        return irService.createIR(inv);
    }

    // ================= GET ALL INVOICES =================
    @GetMapping
    public List<Invoice> getInvoices() {
        // ✔ Returns List<Invoice>
        return irService.getAllIRs();
    }

    // ================= GET BY IR NUMBER =================
    @GetMapping("/{ir}")
    public Invoice getByInvoiceNumber(@PathVariable("ir") String irNumber) {
        // ✔ PathVariable correctly mapped
        return irService.getByInvoiceNumber(irNumber);
    }

    // ================= UPDATE BY IR NUMBER =================
    @PutMapping("/{ir}")
    public Invoice updateInvoice(
            @PathVariable("ir") String irNumber,
            @RequestBody Invoice updatedInvoice) {

        // ✔ No ResponseEntity (as requested)
        return irService.updateByIRNumber(updatedInvoice, irNumber);
    }

    // ================= DELETE BY IR NUMBER =================
    @DeleteMapping("/{ir}")
    public String deleteByIRnumber(@PathVariable("ir") String irNumber) {
        irService.deleteByIRnumber(irNumber);
        return "IR " + irNumber + " Successfully Deleted";
    }
}
