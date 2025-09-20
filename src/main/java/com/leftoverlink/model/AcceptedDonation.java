package com.leftoverlink.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptedDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private User ngo;

    private int quantityAccepted;

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

	public int getQuantityAccepted() {
		return quantityAccepted;
	}

	public void setQuantityAccepted(int quantityAccepted) {
		this.quantityAccepted = quantityAccepted;
	}

	public LocalDateTime getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(LocalDateTime acceptedAt) {
		this.acceptedAt = acceptedAt;
	}

	private LocalDateTime acceptedAt;
}
