package com.example.holidayswap.domain.dto.request.property;

import lombok.Data;

@Data
public class PropertyImageRequest {
    private Long propertyId;
    private String link;
}
