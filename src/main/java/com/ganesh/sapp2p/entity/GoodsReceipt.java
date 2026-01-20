package com.ganesh.sapp2p.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class GoodsReceipt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private String grNumber;
	@Column(nullable = false)
	private String poNumber;
	@Column(nullable = false)
	private int receivedQuantity;
	@Column(nullable = false)
	private String grStatus;
	

}
