package com.leftoverlink.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)  // Optional: Hide null fields in JSON
public class FoodSearchResponse {
    private Long id;
    private String foodName;
    private int quantity;
    private String status;
    private String donorName;
    private String acceptedByName;
    private double distanceKm;
    private String googleMapsUrl;
    private LocalDateTime expiryDate;
    private Integer safeHours; // for time-left display




    public Integer getSafeHours() {
		return safeHours;
	}


	public void setSafeHours(Integer safeHours) {
		this.safeHours = safeHours;
	}


	public Long getId() {
		return id;
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


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getDonorName() {
		return donorName;
	}


	public void setDonorName(String donorName) {
		this.donorName = donorName;
	}


	public String getAcceptedByName() {
		return acceptedByName;
	}


	public void setAcceptedByName(String acceptedByName) {
		this.acceptedByName = acceptedByName;
	}


	public double getDistanceKm() {
		return distanceKm;
	}


	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}


	public String getGoogleMapsUrl() {
		return googleMapsUrl;
	}


	public void setGoogleMapsUrl(String googleMapsUrl) {
		this.googleMapsUrl = googleMapsUrl;
	}


	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}
	public FoodSearchResponse()
	{
		
	}

	public FoodSearchResponse(Long id, String foodName, int quantity, String status, String donorName,
			String acceptedByName, double distanceKm, String googleMapsUrl, LocalDateTime expiryDate) {
		super();
		this.id = id;
		this.foodName = foodName;
		this.quantity = quantity;
		this.status = status;
		this.donorName = donorName;
		this.acceptedByName = acceptedByName;
		this.distanceKm = distanceKm;
		this.googleMapsUrl = googleMapsUrl;
		this.expiryDate = expiryDate;
	}


	public static String generateGoogleMapsUrl(String lat, String lon) {
        if (lat == null || lon == null) return "";
        return "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lon;
    }
}
