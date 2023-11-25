package com.example.holidayswap.domain.entity.property.rating;

import com.example.holidayswap.domain.entity.auth.User;
import com.example.holidayswap.domain.entity.booking.Booking;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rating", schema = "public")
public class Rating {
    @EmbeddedId
//    @Column(name = "rating_id", nullable = false)
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private RatingId id;
    @MapsId("userId")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @MapsId("book_id")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Booking booking;
    private String comment;
    @NotNull
    @PositiveOrZero
    @Max(value = 5, message = "Value maximum is 5")
    private double rating;
    @Enumerated(EnumType.STRING)
    private RatingType ratingType;
    private Date createDate;
    private Date updateDate;


}
