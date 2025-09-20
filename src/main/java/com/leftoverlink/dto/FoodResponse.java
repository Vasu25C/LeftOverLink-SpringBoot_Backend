package com.leftoverlink.dto;

public class FoodResponse {
	 private Long id;
	    private String foodName;
	    private int quantity;
	    private String status;
	    private String donorName;
	    private String acceptedByName;
	    private double distanceKm;
	    private String googleMapsUrl;
	    private String imageUrl;

	    public String getImageUrl() {
	        return imageUrl;
	    }

	    public void setImageUrl(String imageUrl) {
	        this.imageUrl = imageUrl;
	    }


    public FoodResponse() {}

    public FoodResponse(String foodName, int quantity, String status) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.status = status;
    }
    
    public FoodResponse(String foodName, int quantity, String status,
            String donorName, double distanceKm, String googleMapsUrl) {
this.foodName = foodName;
this.quantity = quantity;
this.status = status;
this.donorName = donorName;
this.distanceKm = distanceKm;
this.googleMapsUrl = googleMapsUrl;
}

    
 
    public FoodResponse(Long id, String foodName, int quantity, String status,
            String donorName, String acceptedByName,
            double distanceKm, String latitude, String longitude) {
this.id = id;
this.foodName = foodName;
this.quantity = quantity;
this.status = status;
this.donorName = donorName;
this.acceptedByName = acceptedByName;
this.distanceKm = distanceKm;
this.googleMapsUrl = generateMapUrl(latitude, longitude);
}

    public static String generateGoogleMapsUrl(String lat, String lon) {
        if (lat == null || lon == null) return null;
        return "https://www.google.com/maps?q=" + lat + "," + lon + "&hl=es;z=14&output=embed";
    }

    public static String generateMapUrl(String lat, String lon) {
        return generateGoogleMapsUrl(lat, lon);  // just calls the renamed one
    }

 
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}
