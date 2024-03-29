package com.example.holidayswap.repository.property.coOwner;

import com.example.holidayswap.domain.dto.response.resort.OwnerShipResponseDTO;
import com.example.holidayswap.domain.entity.property.coOwner.CoOwner;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository

public interface CoOwnerRepository extends JpaRepository<CoOwner, Long> {
    @Query(value = "select c.* from co_owner c where c.co_owner_id = :co_owner_id and c.is_deleted = false", nativeQuery = true)
    Optional<CoOwner> findByIdAndIsDeletedIsFalse(@Param("co_owner_id") Long coOwnerId);
    @Query(value = """
            select
            	co.*
            from
            	co_owner co
            inner join property p on
            	co.property_id = p.property_id
            inner join resort r on
            	r.resort_id = p.resort_id
            where
            	(:resortId is null
            		or p.property_id = :resortId)
            	and (:propertyId is null
            		or co.property_id = :propertyId)
            	and (:userId is null
            		or co.user_id = :userId)
            	and (:roomId is null
            		or co.room_id = :roomId)
            	and (:coOwnerStatus is null
            		or co.status = :coOwnerStatus)
            	and (:property_status is null
            		or p.status = :property_status)
            	and (:resort_status is null
            		or r.resort_status = :resort_status)
            		and p.is_deleted = false
            		and r.is_deleted = false
            """, nativeQuery = true)
    Page<CoOwner> findAllByResortIdAndPropertyIdAndUserIdAndRoomIdAndStatus(@Param("resortId") Long resortId, @Param("propertyId") Long propertyId, @Param("userId") Long userId, @Param("roomId") String roomId, @Param("coOwnerStatus") String coOwnerStatus, @Param("property_status") String property_status, @Param("resort_status") String resort_status, Pageable pageable);

    @Query(value = """
            select
            	co.*
            from
            	co_owner co
            inner join property p on
            	co.property_id = p.property_id
            inner join resort r on
            	r.resort_id = p.resort_id
            where
            	(:resortId is null
            		or p.property_id = :resortId)
            	and (:propertyId is null
            		or co.property_id = :propertyId)
            	and (:userId is null
            		or co.user_id = :userId)
            	and (:roomId is null
            		or co.room_id = :roomId)
            	and (:coOwnerStatus is null
            		or co.status = :coOwnerStatus)
            		and p.is_deleted = false
            		and r.is_deleted = false
             """, nativeQuery = true)
    Page<CoOwner> findAllByResortIdAndPropertyIdAndUserIdAndRoomIdAndStatus(@Param("resortId") Long resortId, @Param("propertyId") Long propertyId, @Param("userId") Long userId, @Param("roomId") String roomId, @Param("coOwnerStatus") String coOwnerStatus, Pageable pageable);

    @Query(value = """
            select new com.example.holidayswap.domain.entity.property.coOwner.CoOwner
            (co.propertyId, co.property, co.roomId)
            from CoOwner co
            inner join co.property p
            inner join p.resort r
            where co.status = 'ACCEPTED'
            and p.isDeleted = false
            and p.status = 'ACTIVE'
            and r.isDeleted = false
            and r.status = 'ACTIVE'
            and (:propertyId is null or co.propertyId = :propertyId)
            and (:roomId is null or co.roomId = :roomId)
            group by co.propertyId, co.property, co.roomId
             """)
    Page<CoOwner> findAllByPropertyAndRoomId(@Param("propertyId") Long propertyId, @Param("roomId") String roomId, Pageable pageable);

//    @Query("""
//            select o from CoOwner o
//            where o.id.propertyId = :propertyId
//            and o.id.userId = :userId
//            and o.id.roomId = :roomId
//            and o.isDeleted = false""")
//    Optional<CoOwner> findAllByPropertyIdAndUserIdAndRoomIdAndIsDeleteIsFalse(@Param("propertyId") Long propertyId, @Param("userId") Long userId, @Param("roomId") String roomId);
//
//    @Query("select o from CoOwner o " + "where upper(o.id.roomId) = upper( :roomId)" + "and o.id.propertyId = :propertyId " + "and o.id.userId = :userId " + "and o.isDeleted = false ")
//    Optional<CoOwner> findByPropertyIdAndUserIdAndIdRoomId(@Param("propertyId") Long propertyId, @Param("userId") Long userId, @Param("roomId") String roomId);

    @Query(value = """
                   SELECT O.*
            FROM CO_OWNER O
            WHERE UPPER(O.ROOM_ID) = UPPER(:roomId)
              AND O.PROPERTY_ID = :propertyId
              AND O.USER_ID != :userId
              AND O.IS_DELETED = FALSE
              AND ((O.START_TIME BETWEEN :startTime AND :endTime)
                OR
                   (O.END_TIME BETWEEN :startTime AND :endTime)
                OR
                   (O.START_TIME < :startTime AND O.END_TIME > :endTime)
                OR
                   (O.END_TIME > :startTime AND O.END_TIME < :endTime)
                )
                        """, nativeQuery = true)
    List<CoOwner> checkOverlapsTimeOwnership(@Param("propertyId") Long propertyId, @Param("userId") Long userId, @Param("roomId") String roomId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query(value = "select c.* from co_owner c where c.property_id = ?1 and c.room_id = ?2", nativeQuery = true)
    List<CoOwner> findByPropertyIdAndRoomId(Long propertyId, String apartmentId);

    @Query(value = "SELECT Distinct o.property_id, o.room_id from co_owner o", nativeQuery = true)
    List<OwnerShipResponseDTO> getAllDistinctOwnerShipWithoutUserId();

    @Query(value = """
            select co.* from co_owner co where co.property_id = :propertyId and co.user_id = :userId and co.room_id = :roomId and case when co.type = 'DEEDED' then(   ((:coOwnerStatus is null) or (co.status = :coOwnerStatus))) else ( ((:coOwnerStatus is null) or (co.status = :coOwnerStatus)) and extract(year from date(:startTime)) >= extract(year from date(co.start_time)) and extract(year from date(:endTime)) <= extract(year from date(co.end_time)) ) end
            """, nativeQuery = true)
    Optional<CoOwner> isMatchingCoOwner(@Param("propertyId") Long propertyId, @Param("userId") Long userId, @Param("roomId") String roomId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("coOwnerStatus") String coOwnerStatus);

    @Query(value = """
            SELECT co_owner.* FROM co_owner join property ON property.property_id = co_owner.property_id
            join resort ON resort.resort_id = property.resort_id
            where resort.resort_id = ?1 and resort.resort_status = 'ACTIVE'
            """, nativeQuery = true)
    List<CoOwner> getListCownerByResortId(Long resortId);

    @Query(value = """
            SELECT co_owner.* FROM co_owner join property on property.property_id = co_owner.property_id
                                           where property.property_id = ?1 and property.is_deleted = false
            """, nativeQuery = true)
    List<CoOwner> getListCoOwnerByPropertyId(Long propertyId);

    @Query(value = """
            SELECT co_owner.* FROM co_owner join property on property.property_id = co_owner.property_id
                                           where property.property_id = ?1 and co_owner.room_id = ?2 and property.is_deleted = false
            """, nativeQuery = true)
    List<CoOwner> getListCoOwnerByPropertyIdAndApartmentId(Long propertyId, String apartmentId);

    @Query(value = """
            select co.*
            from co_owner co
            where co.property_id = :propertyId
              and co.user_id = :userId
              and co.room_id = :roomId
              and co.status = 'ACCEPTED'
              and case
                      when :type = 'RIGHT_TO_USE'
                          then
                          extract(year from cast(:startTime as date)) = extract(year from co.start_time)
                              and extract(year from cast(:endTime as date)) = extract(year from co.end_time)
                              and co.type = :type
                      else
                          co.type = :type
                end
            """, nativeQuery = true)
    Optional<CoOwner> findByPropertyIdAndUserIdAndRoomIdAndType(@Param("propertyId") Long propertyId,
                                                                @Param("userId") Long userId,
                                                                @Param("roomId") String roomId,
                                                                @Param("type") String type,
                                                                @Param("startTime") LocalDate startTime,
                                                                @Param("endTime") LocalDate endTime);

    @Query(value = "select c.* from co_owner c where c.co_owner_id = ?1 and c.is_deleted = false", nativeQuery = true)
    Optional<CoOwner> findByIdAndDeleted(Long id);
}
