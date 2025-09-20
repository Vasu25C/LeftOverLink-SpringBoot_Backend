package com.leftoverlink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReportRow {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String address;
    private String pincode;
}
