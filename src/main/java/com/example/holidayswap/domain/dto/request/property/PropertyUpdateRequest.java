package com.example.holidayswap.domain.dto.request.property;

import com.example.holidayswap.domain.dto.request.property.inRoomAmenity.PropertyInRoomAmenityRequest;
import lombok.Data;

import java.util.List;

@Data
public class PropertyUpdateRequest {
    private Long propertyTypeId;
    private Long resortId;
    private int viewId;
    private int kingBeds;
    private int qeenBeds;
    private int twinBeds;
    private int fullBeds;
    private int sofaBeds;
    private int murphyBeds;
    private int numberBedsRoom;
    private int numberBathRoom;
    private PropertyContractRequest propertyContractRequest;
    private List<PropertyImageRequest> propertyImageRequests;
    private List<PropertyInRoomAmenityRequest> propertyInRoomAmenityRequests;
}