package com.example.holidayswap.service.property.rating;

import com.example.holidayswap.domain.dto.request.property.rating.RatingRequest;
import com.example.holidayswap.domain.dto.response.property.rating.RatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingService {
    Page<RatingResponse> getListRatingByPropertyId(Long propertyId, String roomId, Pageable pageable);

    Double getRatingOfProperty(Long propertyId, String roomId);

    RatingResponse getRatingByBookingId(Long bookingId);

    void create(Long bookingId, Long userId, RatingRequest ratingRequest);

    void update(Long bookingId, Long userId, RatingRequest ratingRequest);

    void deleteRatingById(Long bookingId, Long userId);
}
