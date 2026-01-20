package com.ganesh.sapp2p.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.ganesh.sapp2p.entity.GoodsReceipt;
import com.ganesh.sapp2p.repository.GoodsReceiptRepository;
import com.ganesh.sapp2p.service.GoodsReceiptService;
@RestController
@RequestMapping("/api/gr")
public class GoodsReceiptController {

    private final GoodsReceiptService grService;

    public GoodsReceiptController(GoodsReceiptService grService) {
        this.grService = grService;
    }

    // CREATE GR
    @PostMapping
    public GoodsReceipt createGR(@RequestBody GoodsReceipt gr) {
        return grService.createGR(gr);
    }

    // GET ALL GRs
    @GetMapping
    public List<GoodsReceipt> getAllGRs() {
        return grService.getAllGRs();
    }

    // GET GR BY GR NUMBER
    @GetMapping("/{grNumber}")
    public GoodsReceipt getByGrNumber(@PathVariable("grNumber") String grNumber) {
        return grService.getByGrNumber(grNumber);
    }

    // UPDATE GR BY GR NUMBER
    @PutMapping("/{grNumber}")
    public GoodsReceipt updateByGrNumber(@PathVariable("grNumber") String grNumber,
                                         @RequestBody GoodsReceipt gr) {
        return grService.updateByGrNumber(gr, grNumber);
    }

    // DELETE GR BY GR NUMBER
    @DeleteMapping("/{grNumber}")
    public String deleteByGrNumber(@PathVariable("grNumber") String grNumber) {
        grService.deleteByGrNumber(grNumber);
        return "GR " + grNumber + " Successfully Deleted"; 
    }
}
