package com.example.holidayswap.domain.dto.response.property;

import com.example.holidayswap.domain.dto.response.property.amenity.InRoomAmenityTypeResponse;
import com.example.holidayswap.domain.dto.response.resort.ResortResponse;
import com.example.holidayswap.domain.entity.property.PropertyStatus;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PropertyResponse {
    private Long id;
    private String propertyName;
    private String propertyDescription;
    private int numberKingBeds;
    private int numberQueenBeds;
    private int numberSingleBeds;
    private int numberDoubleBeds;
    private int numberTwinBeds;
    private int numberFullBeds;
    private int numberSofaBeds;
    private int numberMurphyBeds;
    private int numberBedsRoom;
    private int numberBathRoom;
    private double roomSize;
    private Boolean isDeleted;
    private PropertyStatus status;
    private Long resortId;
    private ResortResponse resort;
    private PropertyTypeResponse propertyType;
    private PropertyViewResponse propertyView;
    private List<InRoomAmenityTypeResponse> inRoomAmenityType;
    private List<PropertyImageResponse> propertyImages;
    private Double rating;
    private Set<PropertyMaintenanceResponse> propertyMaintenance;
}
