package com.leftoverlink.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String foodName;
    private int quantity;
 // OLD
 // private String expiryDate;

 // NEW
 private LocalDate expiryDate;
 
 @Column
 private Integer safeHours; // Add this field

 
 @Column(nullable = false)
 private boolean donorCertificateSent = false;

 @Column(nullable = false)
 private boolean ngoCertificateSent = false;


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

public List<FoodAcceptance> getAcceptances() {
	return acceptances;
}

public Integer getSafeHours() {
	return safeHours;
}

public void setSafeHours(Integer safeHours) {
	this.safeHours = safeHours;
}

public void setAcceptances(List<FoodAcceptance> acceptances) {
	this.acceptances = acceptances;
}

public int getRemainingQuantity() {
	return remainingQuantity;
}

public void setRemainingQuantity(int remainingQuantity) {
	this.remainingQuantity = remainingQuantity;
}

public String getImageUrl() {
	return imageUrl;
}

public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}

private int remainingQuantity; // New field
private String imageUrl;       // For uploaded image

@OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
private List<FoodAcceptance> acceptances;


    private String status = "POSTED";

    @ManyToOne
    private User donor;
   
  
    @ManyToOne
    @JoinColumn(name = "accepted_by_id")
    private User acceptedBy;

    @Column(name = "accepted_by") // This is the email string column
    private String acceptedByEmail;

    private LocalDate acceptedDate;  // or LocalDateTime

    @Column
    private String latitude;

    @Column
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
 
	public Long getId() {
		return id;
	}

	public String getAcceptedByEmail() {
		return acceptedByEmail;
	}

	public void setAcceptedByEmail(String acceptedByEmail) {
		this.acceptedByEmail = acceptedByEmail;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public LocalDate getExpiryDate() {
	    return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
	    this.expiryDate = expiryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getDonor() {
		return donor;
	}

	public void setDonor(User donor) {
		this.donor = donor;
	}

	public User getAcceptedBy() {
		return acceptedBy;
	}

	public void setAcceptedBy(User acceptedBy) {
		this.acceptedBy = acceptedBy;
	}

	public LocalDate getAcceptedDate() {
		return acceptedDate;
	}

	public void setAcceptedDate(LocalDate acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	@Override
	public String toString() {
		return "Food [id=" + id + ", foodName=" + foodName + ", quantity=" + quantity + ", expiryDate=" + expiryDate
				+ ", status=" + status + ", donor=" + donor + ", acceptedBy=" + acceptedBy + ", acceptedDate="
				+ acceptedDate + ", getId()=" + getId() + ", getFoodName()=" + getFoodName() + ", getQuantity()="
				+ getQuantity() + ", getExpiryDate()=" + getExpiryDate() + ", getStatus()=" + getStatus()
				+ ", getDonor()=" + getDonor() + ", getAcceptedBy()=" + getAcceptedBy() + ", getAcceptedDate()="
				+ getAcceptedDate() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}


	
    
    
}
