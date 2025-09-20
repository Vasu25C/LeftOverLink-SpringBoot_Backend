package com.leftoverlink.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class FoodPostJsonRequest {

    @Schema(description = "Name of the food", example = "Chapati")
    private String foodName;

    @Schema(description = "Quantity of food", example = "100")
    private int quantity;

    @Schema(description = "Expiry date in YYYY-MM-DD format", example = "2025-06-27")
    private String expiryDate;

    @Schema(description = "Donor's email address", example = "donor@example.com")
    private String email;

    @Schema(description = "Optional base64-encoded image string") // ✅ NEW
    private String imageBase64;

    private Integer safeHours;

    public Integer getSafeHours() {
		return safeHours;
	}

	public void setSafeHours(Integer safeHours) {
		this.safeHours = safeHours;
	}

	// Getters and Setters
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

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
