package com.ganesh.sapp2p.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="purchase_order")
public class PurchaseOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true,nullable=false)
	private String poNumber;
	@Column(nullable=false)
	private String prNumber;
	@Column(nullable=false)
	private int quantity;
	@Column(nullable=false)
	private double netPrice;
	@Column(nullable=false)
	private String vendorName;
	
}
