package com.example.holidayswap.repository.property.timeFrame;

import com.example.holidayswap.domain.entity.property.timeFrame.TimeFrame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeFrameRepository extends JpaRepository<TimeFrame, Long> {
    @Query("select v from TimeFrame v where v.coOwner.property.id = ?1 and v.isDeleted = false ")
    Page<TimeFrame> findAllByPropertyId(Long propertyId, Pageable pageable);

    @Query("""
            select v from TimeFrame v
            join v.coOwner o
            join o.property p
            join p.propertyType pT
            join pT.resorts s
            where s.id = ?1
            and v.isDeleted = false""")
    Page<TimeFrame> findAllByResortId(Long resortId, Pageable pageable);

    @Query("select v from TimeFrame v where v.id = ?1 and v.isDeleted = false")
    Optional<TimeFrame> findByIdAndIsDeletedIsFalse(Long id);

    @Query(value = """
            select tf from TimeFrame tf
            where tf.propertyId = :propertyId
            and tf.roomId =:roomId
            and tf.weekNumber = :numberWeek
            """)
    List<TimeFrame> findOverlapWith(
            @Param("propertyId") Long propertyId,
            @Param("roomId") String roomId,
            @Param("numberWeek") int numberWeek
    );

    @Query(value = """
            select tf from TimeFrame tf
            where tf.propertyId = :propertyId
            and tf.userId=:userId
            and tf.roomId =:roomId
            and tf.weekNumber = :numberWeek
            and tf.status != 'REJECTED'
            """)
    Optional<TimeFrame> findOverlapWithStatusIsNotReject(
            @Param("propertyId") Long propertyId,
            @Param("userId") Long userId,
            @Param("roomId") String roomId,
            @Param("numberWeek") int numberWeek
    );

    @Query(value = """
            select time_frame_id, is_deleted, property_id, room_id, status, user_id, week_number
                                                                      from time_frame tf
                                                                      where tf.time_frame_id = :timeFrameId
                                                                        and ((:timeFrameStatus is null) or (tf.status = :timeFrameStatus))
                                                                        and extract(week from date(:startTime)) = tf.week_number
                                                                        and extract(week from date(:endTime)) = tf.week_number
            """, nativeQuery = true)
    Optional<TimeFrame> isMatchingTimeFrames(
            @Param("timeFrameId") Long timeFrameId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("timeFrameStatus") String timeFrameStatus
    );

}
