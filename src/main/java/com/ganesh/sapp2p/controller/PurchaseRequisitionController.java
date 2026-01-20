package com.ganesh.sapp2p.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.ganesh.sapp2p.entity.PurchaseRequisition;
import com.ganesh.sapp2p.repository.PurchaseRequisitionRepository;
import com.ganesh.sapp2p.service.PurchaseRequisitionService;

@RestController
@RequestMapping("/api/pr")
public class PurchaseRequisitionController {

	private final PurchaseRequisitionService prService;

	public PurchaseRequisitionController(PurchaseRequisitionService prService) {
		this.prService = prService;
	}

	@PostMapping
	public PurchaseRequisition createPR(@RequestBody PurchaseRequisition pr) {
		return prService.createPR(pr);
	}

	@GetMapping
	public List<PurchaseRequisition> getAllPRs() {
		return prService.getAllPRs();
	}

	@GetMapping("/{prNumber}")
	public PurchaseRequisition getByPrNumber(@PathVariable String prNumber) {
		return prService.getByPrNumber(prNumber);
	}

	@PutMapping("/{prNumber}")
	public PurchaseRequisition updatePrByPrNumber(@RequestBody PurchaseRequisition pr, @PathVariable String prNumber) {
		return prService.updatePrByPrNumber(prNumber, pr);
	}

	@DeleteMapping("/{prNumber}")
	public String deleteByPrNmber(@PathVariable String prNumber) {
		prService.deleteByPrNmber(prNumber);
		return "PR " + prNumber + " Successfully Deleted";
	}
}
