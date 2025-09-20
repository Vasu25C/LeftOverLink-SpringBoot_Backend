package com.leftoverlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NGODonationResponse {
	/*private String foodName;
    private int quantity;
    private String status;
    private String postedBy;
    private String acceptedDate;
    private double distanceKm; */ // Add this field
	
	    private Long foodId;
	    private String foodName;
	    private int acceptedQuantity;
	    private String status;
	    private String expiryDate;
	    private String donorName;
	    private String donorEmail;
	    private String donorPhone;
	    private String donorAddress;
	    private String imageUrl;
		public Long getFoodId() {
			return foodId;
		}
		public void setFoodId(Long foodId) {
			this.foodId = foodId;
		}
		public String getFoodName() {
			return foodName;
		}
		public void setFoodName(String foodName) {
			this.foodName = foodName;
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
		public String getExpiryDate() {
			return expiryDate;
		}
		public void setExpiryDate(String expiryDate) {
			this.expiryDate = expiryDate;
		}
		public String getDonorName() {
			return donorName;
		}
		public void setDonorName(String donorName) {
			this.donorName = donorName;
		}
		public String getDonorEmail() {
			return donorEmail;
		}
		public void setDonorEmail(String donorEmail) {
			this.donorEmail = donorEmail;
		}
		public String getDonorPhone() {
			return donorPhone;
		}
		public void setDonorPhone(String donorPhone) {
			this.donorPhone = donorPhone;
		}
		public String getDonorAddress() {
			return donorAddress;
		}
		public void setDonorAddress(String donorAddress) {
			this.donorAddress = donorAddress;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public NGODonationResponse(Long foodId, String foodName, int acceptedQuantity, String status, String expiryDate,
				String donorName, String donorEmail, String donorPhone, String donorAddress, String imageUrl) {
			super();
			this.foodId = foodId;
			this.foodName = foodName;
			this.acceptedQuantity = acceptedQuantity;
			this.status = status;
			this.expiryDate = expiryDate;
			this.donorName = donorName;
			this.donorEmail = donorEmail;
			this.donorPhone = donorPhone;
			this.donorAddress = donorAddress;
			this.imageUrl = imageUrl;
		}
	

    
    /*
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


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getPostedBy() {
		return postedBy;
	}


	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}


	public String getAcceptedDate() {
		return acceptedDate;
	}


	public void setAcceptedDate(String acceptedDate) {
		this.acceptedDate = acceptedDate;
	}
	


	public double getDistanceKm() {
		return distanceKm;
	}


	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}


	public NGODonationResponse(String foodName, int quantity, String status, String postedBy, String acceptedDate, double distanceKm) {
	    this.foodName = foodName;
	    this.quantity = quantity;
	    this.status = status;
	    this.postedBy = postedBy;
	    this.acceptedDate = acceptedDate;
	    this.distanceKm = distanceKm;
	}
*/

    
    
}
