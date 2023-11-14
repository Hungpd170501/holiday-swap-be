package com.example.holidayswap.domain.entity.property.rating;

import com.example.holidayswap.domain.entity.auth.User;
import com.example.holidayswap.domain.entity.property.timeFrame.AvailableTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rating", schema = "public")
public class Rating {
    @EmbeddedId
    private RatingId id;
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("availableTimeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "available_time_id", nullable = false)
    private AvailableTime availableTime;
    private String comment;
    @NotNull
    @PositiveOrZero
    @Max(value = 5, message = "Value maximum is 5")
    private double rating;
    @Enumerated(EnumType.STRING)
    private RatingType ratingType;
}