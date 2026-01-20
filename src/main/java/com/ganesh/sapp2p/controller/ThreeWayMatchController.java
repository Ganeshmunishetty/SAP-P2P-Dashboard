package com.ganesh.sapp2p.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ganesh.sapp2p.entity.Invoice;
import com.ganesh.sapp2p.service.ThreeWayMatchService;
@RestController
@RequestMapping("/api/match")
public class ThreeWayMatchController {

    private final ThreeWayMatchService matchService;

    public ThreeWayMatchController(ThreeWayMatchService matchService) {
        this.matchService = matchService;
    }

    // Perform 3-way match for a single invoice
    @PostMapping("/{invoiceNumber}")
    public Invoice matchInvoice(@PathVariable String invoiceNumber) {
        return matchService.performThreeWayMatching(invoiceNumber);
    }
}
