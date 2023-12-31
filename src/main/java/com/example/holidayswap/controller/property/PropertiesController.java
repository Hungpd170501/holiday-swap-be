package com.example.holidayswap.controller.property;

import com.example.holidayswap.domain.dto.request.property.PropertyRegisterRequest;
import com.example.holidayswap.domain.dto.request.property.PropertyRequestUpdate;
import com.example.holidayswap.domain.dto.request.property.PropertyUpdateRequest;
import com.example.holidayswap.domain.dto.request.resort.ResortRequestUpdate;
import com.example.holidayswap.domain.dto.response.property.PropertyImageResponse;
import com.example.holidayswap.domain.dto.response.property.PropertyResponse;
import com.example.holidayswap.domain.dto.response.property.amenity.InRoomAmenityResponse;
import com.example.holidayswap.domain.dto.response.property.amenity.InRoomAmenityTypeResponse;
import com.example.holidayswap.domain.entity.property.PropertyStatus;
import com.example.holidayswap.service.property.PropertyImageService;
import com.example.holidayswap.service.property.PropertyService;
import com.example.holidayswap.service.property.amenity.InRoomAmenityService;
import com.example.holidayswap.service.property.amenity.InRoomAmenityTypeService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/properties")
public class PropertiesController {
    private final PropertyService propertyService;
    private final InRoomAmenityTypeService inRoomAmenityTypeService;
    private final InRoomAmenityService inRoomAmenityService;
    private final PropertyImageService propertyImageService;

    @GetMapping
    public ResponseEntity<Page<PropertyResponse>> gets(
            @RequestParam(value = "resortId", required = false) Long[] resortId,
            @RequestParam(value = "propertyName", defaultValue = "") String propertyName,
            @RequestParam(value = "status", required = false, defaultValue = "ACTIVE, DEACTIVATE, NO_LONGER_IN_BUSINESS") PropertyStatus[] propertyStatus,

            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        var properties = propertyService.gets(resortId, propertyName, propertyStatus, pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{propertyId}/property-in-room-amenity-types")
    public ResponseEntity<List<InRoomAmenityTypeResponse>> getPropertyInRoomAmenityTypes(@PathVariable("propertyId") Long propertyId) {
        return ResponseEntity.ok(inRoomAmenityTypeService.gets(propertyId));
    }

    @GetMapping("/{propertyId}/property-image")
    public ResponseEntity<List<PropertyImageResponse>> getPropertyImages(@PathVariable("propertyId") Long propertyId) {
        var dtoResponses = propertyImageService.gets(propertyId);
        return ResponseEntity.ok(dtoResponses);
    }

    @GetMapping("/{propertyId}/property-in-room-amenity-types/{propertyInRoomAmenityTypeId}/property-in-room-amenities")
    public ResponseEntity<List<InRoomAmenityResponse>> getPropertyInRoomAmenities(@PathVariable("propertyId") Long propertyId, @PathVariable("propertyInRoomAmenityTypeId") Long amenityTypeId) {
        return ResponseEntity.ok(inRoomAmenityService.gets(amenityTypeId, propertyId));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> get(@PathVariable("propertyId") Long propertyId) {
        var property = propertyService.get(propertyId);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/getListPropertyActive")
    public ResponseEntity<List<PropertyResponse>> getListPropertyActive(@RequestParam Long resortId) {
        var property = propertyService.getListPropertyActive(resortId);
        return ResponseEntity.ok(property);
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(@RequestPart(name = "property") PropertyRegisterRequest propertyRegisterRequest, @RequestPart List<MultipartFile> propertyImages) {
        var propertyCreated = propertyService.create(propertyRegisterRequest, propertyImages);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(propertyCreated.getId()).toUri();
        return ResponseEntity.created(location).body(propertyService.get(propertyCreated.getId()));
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<Void> update(@PathVariable("propertyId") Long propertyId,
                                       @RequestPart PropertyUpdateRequest propertyUpdateRequest
            , @RequestPart(required = false) List<MultipartFile> propertyImages) {
        propertyService.update(propertyId, propertyUpdateRequest, propertyImages);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(propertyId).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{propertyId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("propertyId") Long propertyId, @RequestBody PropertyStatus propertyStatus) {
        propertyService.update(propertyId, propertyStatus);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(propertyId).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> delete(@PathVariable("propertyId") Long propertyId) {
        propertyService.delete(propertyId, new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateStatus")
    public  ResponseEntity<Void> updateStatusProperty(@RequestPart PropertyRequestUpdate resortUpdateRequest,
                                                    @RequestPart(required = false) List<MultipartFile> resortImage) throws MessagingException, IOException {
        propertyService.updateStatus(resortUpdateRequest.getPropertyId(), resortUpdateRequest.getResortStatus(), resortUpdateRequest.getStartDate(), resortUpdateRequest.getEndDate(), resortImage);
        return ResponseEntity.noContent().build();
    }

}
