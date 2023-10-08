package com.example.holidayswap.repository.property.coOwner;

import com.example.holidayswap.domain.entity.property.coOwner.CoOwner;
import com.example.holidayswap.domain.entity.property.coOwner.CoOwnerId;
import com.example.holidayswap.domain.entity.property.coOwner.CoOwnerStatus;
import com.example.holidayswap.domain.entity.property.coOwner.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository

public interface CoOwnerRepository extends JpaRepository<CoOwner, CoOwnerId> {
    @Query("select o from CoOwner o where o.property.id = ?1 and o.isDeleted = false")
    List<CoOwner> findAllByPropertyIdAndIsDeletedIsFalse(Long propertyId);

    @Query("select o from CoOwner o where o.user.userId = ?1 and o.isDeleted = false")
    List<CoOwner> findAllByUserIdAndIsDeletedIsFalse(Long userId);

    @Query("""
            select o from CoOwner o
            where o.id.propertyId = :propertyId
            and o.id.userId = :userId
            and o.id.roomId = :roomId
            and o.isDeleted = false""")
    Optional<CoOwner> findAllByPropertyIdAndUserIdAndRoomIdAndIsDeleteIsFalse(
            @Param("propertyId") Long propertyId,
            @Param("userId") Long userId,
            @Param("roomId") String roomId);

    @Query("select o from CoOwner o " +
           "where upper(o.id.roomId) = upper( :roomId)" +
           "and o.id.propertyId = :propertyId " +
           "and o.id.userId = :userId " +
           "and o.isDeleted = false ")
    Optional<CoOwner> findByPropertyIdAndUserUserIdAndIdRoomId(@Param("propertyId") Long propertyId,
                                                               @Param("userId") Long userId,
                                                               @Param("roomId") String roomId);

    @Query("select o from CoOwner o " +
           "where upper(o.id.roomId) = upper( :roomId)" +
           "and o.id.propertyId = :propertyId " +
           "and o.isDeleted = false ")
    List<CoOwner> findByPropertyIdAndIdRoomId(@Param("propertyId") Long propertyId, @Param("roomId") String roomId);

    @Query(value = """
            SELECT PROPERTY_ID,
                                 ROOM_ID,
                                 USER_ID,
                                 END_TIME,
                                 IS_DELETED,
                                 START_TIME,
                                 STATUS,
                                 TYPE
                          FROM CO_OWNER O
                          WHERE UPPER(O.ROOM_ID) = UPPER(:roomId)
                            AND O.PROPERTY_ID = :propertyId
                            AND O.USER_ID != :userId
                            AND O.IS_DELETED = FALSE
                            AND CASE
                                    WHEN O.TYPE = 'RIGHT_TO_USE'
                                        then ((O.START_TIME BETWEEN :startTime AND :endTime)
                                            OR
                                             (O.END_TIME BETWEEN :startTime AND :endTime)
                                            OR
                                             (O.START_TIME < :startTime AND O.END_TIME > :endTime)
                                            OR
                                             (O.END_TIME > :startTime AND O.END_TIME < :endTime)
                                        )
                                    ELSE O.IS_DELETED = FALSE and O.TYPE = 'DEEDED'
                              END
            """, nativeQuery = true)
    List<CoOwner> checkOverlapsTimeOwnership(@Param("propertyId") Long propertyId,
                                                 @Param("userId") Long userId,
                                                 @Param("roomId") String roomId,
                                                 @Param("startTime") Date startTime,
                                                 @Param("endTime") Date endTime
    );

    @Query("""
            select o from CoOwner o
            where o.id.propertyId = ?1
            and o.id.userId = ?2
            and o.id.roomId = ?3
            and o.startTime >= ?4
            and o.endTime <= ?5
            and o.type = ?6
            and o.status = ?7
            and o.isDeleted = false""")
    Optional<CoOwner> findByTypeIsRightToUse(
            Long propertyId,
            Long userId,
            String roomId,
            Date startTime,
            Date endTime,
            ContractType type,
            CoOwnerStatus status);

    @Query("""
            select o from CoOwner o
            where o.id.propertyId = ?1
            and o.id.userId = ?2
            and o.id.roomId = ?3
            and o.type = ?4
            and o.status = ?5
            and o.isDeleted = false""")
    Optional<CoOwner> findByTypeIsDeeded(
            Long propertyId,
            Long userId,
            String roomId,
            ContractType type,
            CoOwnerStatus status);
}
