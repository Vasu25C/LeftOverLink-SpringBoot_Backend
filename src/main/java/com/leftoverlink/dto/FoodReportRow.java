package com.leftoverlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoodReportRow {
    private Long id;
    private String foodName;
    private int quantity;
    private String status;
    private String donorName;
    private String acceptedByName;
    private String expiryDate;
}
