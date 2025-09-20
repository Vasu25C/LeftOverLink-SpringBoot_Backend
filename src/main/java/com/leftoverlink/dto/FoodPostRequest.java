package com.leftoverlink.dto;

import lombok.Data;

@Data
public class FoodPostRequest {
    private String foodName;
    private int quantity;
    private String expiryDate;
    private String email;
}
