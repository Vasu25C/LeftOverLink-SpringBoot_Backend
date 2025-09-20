package com.leftoverlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptanceReport {
    private Long id;            // ✅ Unique ID for DataGrid (typically from FoodAcceptance entity)
    private Long foodId;
    private String foodName;
    private String ngoName;
    private int quantity;
    private String status;
    
    
	public AcceptanceReport(Long id, Long foodId, String foodName, String ngoName, int quantity, String status) {
		super();
		this.id = id;
		this.foodId = foodId;
		this.foodName = foodName;
		this.ngoName = ngoName;
		this.quantity = quantity;
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public String getNgoName() {
		return ngoName;
	}
	public void setNgoName(String ngoName) {
		this.ngoName = ngoName;
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
    
    
    
}
