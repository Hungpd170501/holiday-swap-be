package com.example.holidayswap.controller.property;

import java.util.Date;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.holidayswap.domain.dto.response.property.ApartmentForRentResponse;
import com.example.holidayswap.domain.dto.response.property.ResortApartmentForRentResponse;
import com.example.holidayswap.service.property.ApartmentForRentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/apartment-for-rent")
public class ApartmentsForRentController {
    private final ApartmentForRentService roomService;

    @GetMapping
    public ResponseEntity<Page<ApartmentForRentResponse>> gets(
            @RequestParam(value = "locationName", defaultValue = "") String locationName,
            @RequestParam(value = "resortId", required = false) Long resortId,
            @RequestParam(value = "checkIn", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkIn,
            @RequestParam(value = "checkOut", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkOut,
            @RequestParam(name = "min", required = false) Long min,
            @RequestParam(name = "max", required = false) Long max,
            @RequestParam(name = "guest", defaultValue = "1") int guest,
            @RequestParam(name = "numberBedsRoom", defaultValue = "1") int numberBedsRoom,
            @RequestParam(name = "numberBathRoom", defaultValue = "1") int numberBathRoom,
            @RequestParam(name = "listOfInRoomAmenity", required = false) Set<Long> listOfInRoomAmenity,
            @RequestParam(name = "listOfPropertyView", required = false) Set<Long> listOfPropertyView,
            @RequestParam(name = "listOfPropertyType", required = false) Set<Long> listOfPropertyType,
            @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        var dtoResponse = roomService.gets(locationName, resortId, checkIn, checkOut, min, max, guest, numberBedsRoom, numberBathRoom, listOfInRoomAmenity, listOfPropertyView, listOfPropertyType, pageable);
        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/resort")
    public ResponseEntity<Page<ResortApartmentForRentResponse>> gets2(
            @RequestParam(value = "locationName", defaultValue = "") String locationName,

            @RequestParam(value = "checkIn", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkIn,
            @RequestParam(value = "checkOut", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date checkOut,
            @RequestParam(name = "min", required = false) Long min,
            @RequestParam(name = "max", required = false) Long max,
            @RequestParam(name = "guest", defaultValue = "1") int guest,
            @RequestParam(name = "numberBedsRoom", defaultValue = "1") int numberBedsRoom,
            @RequestParam(name = "numberBathRoom", defaultValue = "1") int numberBathRoom,
            @RequestParam(name = "listOfInRoomAmenity", required = false) Set<Long> listOfInRoomAmenity,
            @RequestParam(name = "listOfPropertyView", required = false) Set<Long> listOfPropertyView,
            @RequestParam(name = "listOfPropertyType", required = false) Set<Long> listOfPropertyType,
            @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        var dtoResponse = roomService.getsResort(locationName, checkIn, checkOut, min, max, guest, numberBedsRoom,
                numberBathRoom, listOfInRoomAmenity, listOfPropertyView, listOfPropertyType, pageable);
        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/{availableId}")
    public ResponseEntity<ApartmentForRentResponse> get(@PathVariable("availableId") Long availableId) {
        var dtoResponse = roomService.get(availableId);
        return ResponseEntity.ok(dtoResponse);
    }
}
