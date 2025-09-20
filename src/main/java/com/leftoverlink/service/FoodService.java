package com.leftoverlink.service;

import com.leftoverlink.dto.FoodPostJsonRequest;
import org.springframework.web.multipart.MultipartFile;

import com.leftoverlink.dto.FoodRequest;
import com.leftoverlink.dto.FoodResponse;
import com.leftoverlink.dto.FoodSearchResponse;
import com.leftoverlink.dto.NGODonationResponse;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

	public interface FoodService {

		ResponseEntity<String> postFoodJson(FoodPostJsonRequest request) throws IOException;

	    List<FoodResponse> getMyDonations(String donorEmail);

	    List<FoodSearchResponse> getAvailableFood(String ngoEmail, Double maxDistance);

	    ResponseEntity<String> acceptFood(Long foodId, String ngoEmail, int requestedQuantity);

	    ResponseEntity<String> postFoodJson(FoodPostJsonRequest request, String email) throws IOException;

	    List<NGODonationResponse> getMyAcceptedDonations(String ngoEmail);

	    ResponseEntity<String> markDonationComplete(Long foodId, String ngoEmail);
	}

    
