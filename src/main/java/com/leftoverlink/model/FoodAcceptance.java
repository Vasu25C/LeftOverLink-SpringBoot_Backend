package com.leftoverlink.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodAcceptance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Food food;

    @ManyToOne
    private User ngo;

    private int acceptedQuantity;

    private String status = "ACCEPTED"; // Or "COMPLETED" after mutual confirmation

    private boolean donorConfirmed = false;
    private boolean ngoConfirmed = false;
    private boolean certificateGenerated = false;

    private LocalDateTime acceptedAt = LocalDateTime.now(); // Optional tracking

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public User getNgo() {
		return ngo;
	}

	public void setNgo(User ngo) {
		this.ngo = ngo;
	}

	public int getAcceptedQuantity() {
		return acceptedQuantity;
	}

	public void setAcceptedQuantity(int acceptedQuantity) {
		this.acceptedQuantity = acceptedQuantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDonorConfirmed() {
		return donorConfirmed;
	}

	public void setDonorConfirmed(boolean donorConfirmed) {
		this.donorConfirmed = donorConfirmed;
	}

	public boolean isNgoConfirmed() {
		return ngoConfirmed;
	}

	public void setNgoConfirmed(boolean ngoConfirmed) {
		this.ngoConfirmed = ngoConfirmed;
	}

	public boolean isCertificateGenerated() {
		return certificateGenerated;
	}

	public void setCertificateGenerated(boolean certificateGenerated) {
		this.certificateGenerated = certificateGenerated;
	}

	public LocalDateTime getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(LocalDateTime acceptedAt) {
		this.acceptedAt = acceptedAt;
	}
    
	boolean donorCertificateSent;
	boolean ngoCertificateSent;

	public boolean isDonorCertificateSent() {
		return donorCertificateSent;
	}

	public void setDonorCertificateSent(boolean donorCertificateSent) {
		this.donorCertificateSent = donorCertificateSent;
	}

	public boolean isNgoCertificateSent() {
		return ngoCertificateSent;
	}

	public void setNgoCertificateSent(boolean ngoCertificateSent) {
		this.ngoCertificateSent = ngoCertificateSent;
	}

	
    
}
