package com.example.holidayswap.domain.dto.response.property.inRoomAmenity;

import lombok.Data;

@Data
public class PropertyTypeResponse {
    private Long id;
    private String propertyTypeName;
    private String propertyTypeDescription;
    private boolean isDeleted;
}