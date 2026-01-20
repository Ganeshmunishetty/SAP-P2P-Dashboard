package com.ganesh.sapp2p.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String invoiceNumber;
	@Column(nullable = false)
	private String poNumber;
	private String grNumber;
	@JsonIgnore
	private double invoiceAmount;
	@Column(nullable = false)
	private String invoiceStatus;

}
