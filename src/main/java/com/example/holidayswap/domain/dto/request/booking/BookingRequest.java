package com.example.holidayswap.domain.dto.request.booking;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BookingRequest {
    private Long availableTimeId;
    private Long userId;
    private Date checkInDate;
    private Date checkOutDate;
    private List<UserOfBookingRequest> userOfBookingRequests;
}
