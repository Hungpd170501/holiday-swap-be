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

    @Query("select v from TimeFrame v where v.coOwner.property.id = ?1 and v.isDeleted = false")
    Page<TimeFrame> findAllByPropertyIdAndDeletedIsFalse(Long propertyId, Pageable pageable);

    @Query("select v from TimeFrame v where v.id = ?1 and v.isDeleted = false")
    Optional<TimeFrame> findByIdAndDeletedFalse(Long id);

    @Query("""
            select v from TimeFrame v
            where v.coOwner.property.id = ?1
            and v.isDeleted = false
            and v.startTime >= ?2
            and v.endTime <= ((CAST(?1 AS date)))""")
    List<TimeFrame> findAllByPropertyIdAndDeletedIsFalseAndAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            Long propertyId,
            Date startTime,
            Date endTime);

    @Query("""
            select v from TimeFrame v
            where v.coOwner.property.id = ?1
            and v.isDeleted = false
            and v.startTime >= ?2
            and v.endTime <= ((CAST(?1 AS date)))""")
    Optional<TimeFrame> findByPropertyIdAndDeletedIsFalseAndAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            Long propertyId,
            Date startTime,
            Date endTime);

    @Query(value = """
            SELECT VU.TIME_FRAME_ID,
                   VU.END_TIME,
                   VU.IS_DELETED,
                   VU.START_TIME,
                   VU.STATUS,
                   VU.PROPERTY_ID,
                   VU.USER_ID,
                   VU.ROOM_ID
            FROM TIME_FRAME VU
                     JOIN CO_OWNER O ON VU.PROPERTY_ID = O.PROPERTY_ID AND VU.ROOM_ID = O.ROOM_ID AND VU.USER_ID = O.USER_ID
            WHERE VU.PROPERTY_ID = :propertyId
                AND VU.ROOM_ID = :roomId
                AND VU.IS_DELETED = FALSE
                AND VU.USER_ID != :userId
                AND (:statusVacation IS NULL OR VU.STATUS = :statusVacation)
                AND (:statusOwner IS NULL OR O.STATUS = :statusOwner)
                AND ((CASE
                          WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 4 = 0 AND
                               (EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 100 != 0 OR
                                EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 400 = 0)
                              THEN EXTRACT(DOY FROM DATE(VU.START_TIME))
                          ELSE
                              (CASE
                                   WHEN EXTRACT(MONTH FROM DATE(VU.START_TIME)) > 2
                                       THEN EXTRACT(DOY FROM DATE(VU.START_TIME)) + 1
                                   ELSE EXTRACT(DOY FROM DATE(VU.START_TIME))
                                  END)
                    END) BETWEEN (CASE
                                      WHEN EXTRACT(YEAR FROM DATE(:startTime)) % 4 = 0 AND
                                           (EXTRACT(YEAR FROM DATE(:startTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:startTime)) % 400 = 0)
                                          THEN EXTRACT(DOY FROM DATE(:startTime))
                                      ELSE
                                          (CASE
                                               WHEN EXTRACT(MONTH FROM DATE(:startTime)) > 2
                                                   THEN EXTRACT(DOY FROM DATE(:startTime)) + 1
                                               ELSE EXTRACT(DOY FROM DATE(:startTime))
                                              END)
                    END) AND (CASE
                                  WHEN EXTRACT(YEAR FROM DATE(:startTime)) < EXTRACT(YEAR FROM DATE(:endTime))
                                      THEN
                                      (CASE
                                           WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                                (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                               THEN EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                           ELSE
                                               (CASE
                                                    WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                        THEN EXTRACT(DOY FROM DATE(:endTime)) + 1 + EXTRACT(DOY FROM DATE(:startTime))
                                                    ELSE EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                                   END)
                                          END)
                                  ELSE
                                      (CASE
                                           WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                                (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                               THEN EXTRACT(DOY FROM DATE(:endTime))
                                           ELSE
                                               (CASE
                                                    WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                        THEN EXTRACT(DOY FROM DATE(:endTime)) + 1
                                                    ELSE EXTRACT(DOY FROM DATE(:endTime))
                                                   END)
                                          END)
                    END))
               OR ((CASE
                        WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) < EXTRACT(YEAR FROM DATE(VU.END_TIME))
                            THEN
                            (CASE
                                 WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                      (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                       EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                     THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                 ELSE
                                     (CASE
                                          WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                              THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1 + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                          ELSE EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                         END)
                                END)
                        ELSE
                            (CASE
                                 WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                      (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                       EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                     THEN EXTRACT(DOY FROM DATE(VU.END_TIME))
                                 ELSE
                                     (CASE
                                          WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                              THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1
                                          ELSE EXTRACT(DOY FROM DATE(VU.END_TIME))
                                         END)
                                END)
                END) BETWEEN (CASE
                                  WHEN EXTRACT(YEAR FROM DATE(:startTime)) % 4 = 0 AND
                                       (EXTRACT(YEAR FROM DATE(:startTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:startTime)) % 400 = 0)
                                      THEN EXTRACT(DOY FROM DATE(:startTime))
                                  ELSE
                                      (CASE
                                           WHEN EXTRACT(MONTH FROM DATE(:startTime)) > 2
                                               THEN EXTRACT(DOY FROM DATE(:startTime)) + 1
                                           ELSE EXTRACT(DOY FROM DATE(:startTime))
                                          END)
                END) AND (CASE
                              WHEN EXTRACT(YEAR FROM DATE(:startTime)) < EXTRACT(YEAR FROM DATE(:endTime))
                                  THEN
                                  (CASE
                                       WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                            (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                           THEN EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                       ELSE
                                           (CASE
                                                WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                    THEN EXTRACT(DOY FROM DATE(:endTime)) + 1 + EXTRACT(DOY FROM DATE(:startTime))
                                                ELSE EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                               END)
                                      END)
                              ELSE
                                  (CASE
                                       WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                            (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                           THEN EXTRACT(DOY FROM DATE(:endTime))
                                       ELSE
                                           (CASE
                                                WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                    THEN EXTRACT(DOY FROM DATE(:endTime)) + 1
                                                ELSE EXTRACT(DOY FROM DATE(:endTime))
                                               END)
                                      END)
                END))
               OR ((CASE
                        WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 4 = 0 AND
                             (EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 400 = 0)
                            THEN EXTRACT(DOY FROM DATE(VU.START_TIME))
                        ELSE
                            (CASE
                                 WHEN EXTRACT(MONTH FROM DATE(VU.START_TIME)) > 2
                                     THEN EXTRACT(DOY FROM DATE(VU.START_TIME)) + 1
                                 ELSE EXTRACT(DOY FROM DATE(VU.START_TIME))
                                END)
                END) < (CASE
                            WHEN EXTRACT(YEAR FROM DATE(:startTime)) % 4 = 0 AND
                                 (EXTRACT(YEAR FROM DATE(:startTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:startTime)) % 400 = 0)
                                THEN EXTRACT(DOY FROM DATE(:startTime))
                            ELSE
                                (CASE
                                     WHEN EXTRACT(MONTH FROM DATE(:startTime)) > 2
                                         THEN EXTRACT(DOY FROM DATE(:startTime)) + 1
                                     ELSE EXTRACT(DOY FROM DATE(:startTime))
                                    END)
                END) AND (CASE
                              WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) < EXTRACT(YEAR FROM DATE(VU.END_TIME))
                                  THEN
                                  (CASE
                                       WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                            (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                             EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                           THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                       ELSE
                                           (CASE
                                                WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                                    THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1 +
                                                         EXTRACT(DOY FROM DATE(VU.START_TIME))
                                                ELSE EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                               END)
                                      END)
                              ELSE
                                  (CASE
                                       WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                            (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                             EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                           THEN EXTRACT(DOY FROM DATE(VU.END_TIME))
                                       ELSE
                                           (CASE
                                                WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                                    THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1
                                                ELSE EXTRACT(DOY FROM DATE(VU.END_TIME))
                                               END)
                                      END)
                END) > (CASE
                            WHEN EXTRACT(YEAR FROM DATE(:startTime)) < EXTRACT(YEAR FROM DATE(:endTime))
                                THEN
                                (CASE
                                     WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                          (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                         THEN EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                     ELSE
                                         (CASE
                                              WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                  THEN EXTRACT(DOY FROM DATE(:endTime)) + 1 + EXTRACT(DOY FROM DATE(:startTime))
                                              ELSE EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                             END)
                                    END)
                            ELSE
                                (CASE
                                     WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                          (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                         THEN EXTRACT(DOY FROM DATE(:endTime))
                                     ELSE
                                         (CASE
                                              WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                  THEN EXTRACT(DOY FROM DATE(:endTime)) + 1
                                              ELSE EXTRACT(DOY FROM DATE(:endTime))
                                             END)
                                    END)
                END))
               OR ((CASE
                        WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 4 = 0 AND
                             (EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 400 = 0)
                            THEN EXTRACT(DOY FROM DATE(VU.START_TIME))
                        ELSE
                            (CASE
                                 WHEN EXTRACT(MONTH FROM DATE(VU.START_TIME)) > 2
                                     THEN EXTRACT(DOY FROM DATE(VU.START_TIME)) + 1
                                 ELSE EXTRACT(DOY FROM DATE(VU.START_TIME))
                                END)
                END) > (CASE
                            WHEN EXTRACT(YEAR FROM DATE(:startTime)) % 4 = 0 AND
                                 (EXTRACT(YEAR FROM DATE(:startTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:startTime)) % 400 = 0)
                                THEN EXTRACT(DOY FROM DATE(:startTime))
                            ELSE
                                (CASE
                                     WHEN EXTRACT(MONTH FROM DATE(:startTime)) > 2
                                         THEN EXTRACT(DOY FROM DATE(:startTime)) + 1
                                     ELSE EXTRACT(DOY FROM DATE(:startTime))
                                    END)
                END) AND (CASE
                              WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) < EXTRACT(YEAR FROM DATE(VU.END_TIME))
                                  THEN
                                  (CASE
                                       WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                            (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                             EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                           THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                       ELSE
                                           (CASE
                                                WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                                    THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1 +
                                                         EXTRACT(DOY FROM DATE(VU.START_TIME))
                                                ELSE EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                               END)
                                      END)
                              ELSE
                                  (CASE
                                       WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                            (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                             EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                           THEN EXTRACT(DOY FROM DATE(VU.END_TIME))
                                       ELSE
                                           (CASE
                                                WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                                    THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1
                                                ELSE EXTRACT(DOY FROM DATE(VU.END_TIME))
                                               END)
                                      END)
                END) < (CASE
                            WHEN EXTRACT(YEAR FROM DATE(:startTime)) < EXTRACT(YEAR FROM DATE(:endTime))
                                THEN
                                (CASE
                                     WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                          (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                         THEN EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                     ELSE
                                         (CASE
                                              WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                  THEN EXTRACT(DOY FROM DATE(:endTime)) + 1 + EXTRACT(DOY FROM DATE(:startTime))
                                              ELSE EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                             END)
                                    END)
                            ELSE
                                (CASE
                                     WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                          (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                         THEN EXTRACT(DOY FROM DATE(:endTime))
                                     ELSE
                                         (CASE
                                              WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                  THEN EXTRACT(DOY FROM DATE(:endTime)) + 1
                                              ELSE EXTRACT(DOY FROM DATE(:endTime))
                                             END)
                                    END)
                END))
                """, nativeQuery = true)
    List<TimeFrame> findOverlapWith(
            @Param("propertyId") Long propertyId,
            @Param("userId") Long userId
            , @Param("roomId") String roomId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("statusVacation") String statusVacation,
            @Param("statusOwner") String statusOwnership
    );

    @Query(value = """
            SELECT TIME_FRAME_ID,
                   END_TIME,
                   IS_DELETED,
                   START_TIME,
                   STATUS,
                   PROPERTY_ID,
                   USER_ID,
                   ROOM_ID
            FROM TIME_FRAME VU
            WHERE VU.PROPERTY_ID = :propertyId
              AND VU.USER_ID = :userId
              AND VU.ROOM_ID = :roomId
              AND VU.IS_DELETED = FALSE
              AND VU.STATUS = :vacationStatus
              AND (
                ((CASE
                      WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 4 = 0 AND
                           (EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 100 != 0 OR EXTRACT(YEAR FROM DATE(VU.START_TIME)) % 400 = 0)
                          THEN EXTRACT(DOY FROM DATE(VU.START_TIME))
                      ELSE
                          (CASE
                               WHEN EXTRACT(MONTH FROM DATE(VU.START_TIME)) > 2
                                   THEN EXTRACT(DOY FROM DATE(VU.START_TIME)) + 1
                               ELSE EXTRACT(DOY FROM DATE(VU.START_TIME))
                              END)
                    END) <= (CASE
                                 WHEN EXTRACT(YEAR FROM DATE(:startTime)) % 4 = 0 AND
                                      (EXTRACT(YEAR FROM DATE(:startTime)) % 100 != 0 OR
                                       EXTRACT(YEAR FROM DATE(:startTime)) % 400 = 0)
                                     THEN EXTRACT(DOY FROM DATE(:startTime))
                                 ELSE
                                     (CASE
                                          WHEN EXTRACT(MONTH FROM DATE(:startTime)) > 2
                                              THEN EXTRACT(DOY FROM DATE(:startTime)) + 1
                                          ELSE EXTRACT(DOY FROM DATE(:startTime))
                                         END)
                    END) AND (CASE
                                  WHEN EXTRACT(YEAR FROM DATE(VU.START_TIME)) < EXTRACT(YEAR FROM DATE(VU.END_TIME))
                                      THEN
                                      (CASE
                                           WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                                (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                                 EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                               THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                           ELSE
                                               (CASE
                                                    WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                                        THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1 +
                                                             EXTRACT(DOY FROM DATE(VU.START_TIME))
                                                    ELSE EXTRACT(DOY FROM DATE(VU.END_TIME)) + EXTRACT(DOY FROM DATE(VU.START_TIME))
                                                   END)
                                          END)
                                  ELSE
                                      (CASE
                                           WHEN EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 4 = 0 AND
                                                (EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 100 != 0 OR
                                                 EXTRACT(YEAR FROM DATE(VU.END_TIME)) % 400 = 0)
                                               THEN EXTRACT(DOY FROM DATE(VU.END_TIME))
                                           ELSE
                                               (CASE
                                                    WHEN EXTRACT(MONTH FROM DATE(VU.END_TIME)) > 2
                                                        THEN EXTRACT(DOY FROM DATE(VU.END_TIME)) + 1
                                                    ELSE EXTRACT(DOY FROM DATE(VU.END_TIME))
                                                   END)
                                          END)
                    END) >= (CASE
                                 WHEN EXTRACT(YEAR FROM DATE(:startTime)) < EXTRACT(YEAR FROM DATE(:endTime))
                                     THEN
                                     (CASE
                                          WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                               (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR
                                                EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                              THEN EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                          ELSE
                                              (CASE
                                                   WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                       THEN EXTRACT(DOY FROM DATE(:endTime)) + 1 +
                                                            EXTRACT(DOY FROM DATE(:startTime))
                                                   ELSE EXTRACT(DOY FROM DATE(:endTime)) + EXTRACT(DOY FROM DATE(:startTime))
                                                  END)
                                         END)
                                 ELSE
                                     (CASE
                                          WHEN EXTRACT(YEAR FROM DATE(:endTime)) % 4 = 0 AND
                                               (EXTRACT(YEAR FROM DATE(:endTime)) % 100 != 0 OR
                                                EXTRACT(YEAR FROM DATE(:endTime)) % 400 = 0)
                                              THEN EXTRACT(DOY FROM DATE(:endTime))
                                          ELSE
                                              (CASE
                                                   WHEN EXTRACT(MONTH FROM DATE(:endTime)) > 2
                                                       THEN EXTRACT(DOY FROM DATE(:endTime)) + 1
                                                   ELSE EXTRACT(DOY FROM DATE(:endTime))
                                                  END)
                                         END)
                    END))
                )""", nativeQuery = true)
    Optional<TimeFrame> findByStartTimeAndEndTimeIsInVacationUnitTime(
            @Param("propertyId") Long propertyId,
            @Param("userId") Long userId,
            @Param("roomId") String roomId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("vacationStatus") String vacationStatus
    );
}