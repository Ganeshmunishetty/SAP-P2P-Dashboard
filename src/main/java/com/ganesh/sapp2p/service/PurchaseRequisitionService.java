package com.ganesh.sapp2p.service;

import java.util.List;

import org.apache.commons.lang3.RuntimeEnvironment;
import org.springframework.stereotype.Service;

import com.ganesh.sapp2p.entity.PurchaseRequisition;
import com.ganesh.sapp2p.repository.PurchaseRequisitionRepository;

@Service
public class PurchaseRequisitionService {

	private final PurchaseRequisitionRepository prRepo;

	public PurchaseRequisitionService(PurchaseRequisitionRepository prRepo) {
		this.prRepo = prRepo;
	}

	public PurchaseRequisition createPR(PurchaseRequisition pr) {
		pr.setStatus("PENDING");
		return prRepo.save(pr);
	}

	public List<PurchaseRequisition> getAllPRs() {
		return prRepo.findAll();
	}

	public PurchaseRequisition getByPrNumber(String prNumber) {
		return prRepo.findByPrNumber(prNumber)
				.orElseThrow(() -> new RuntimeException(prNumber + "PR Number Not Found " + prNumber));
	}

	public PurchaseRequisition updatePrByPrNumber(String prNumber, PurchaseRequisition pr) {

		PurchaseRequisition existing= prRepo.findByPrNumber(prNumber)
	            .orElseThrow(() -> new RuntimeException("PR Not Found: " + prNumber));

		existing.setPrNumber(pr.getPrNumber());
		existing.setMaterial(pr.getMaterial());
		existing.setQuantity(pr.getQuantity());
		existing.setBudgetPrice(pr.getBudgetPrice());
		existing.setRequestedBy(pr.getRequestedBy());
//		existing.setStatus(pr.getStatus());

		return prRepo.save(existing);

	}

	public void UpdateStatus(String prnumber,String newStatus) {
		PurchaseRequisition pr=getByPrNumber(prnumber);
		pr.setStatus(newStatus);
		prRepo.save(pr);
	}
	public void deleteByPrNmber(String prNumber) {
		PurchaseRequisition existing = prRepo.findByPrNumber(prNumber)
				.orElseThrow(() -> new RuntimeException(prNumber + " The PR Number Does'nt Exist"));
		prRepo.delete(existing);
	}
}
