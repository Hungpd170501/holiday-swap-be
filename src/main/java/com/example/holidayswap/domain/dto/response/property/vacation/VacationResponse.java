package com.example.holidayswap.domain.dto.response.property.vacation;

import com.example.holidayswap.domain.entity.property.vacation.VacationStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VacationResponse {
    private Long id;
    private Date startTime;
    private Date endTime;
    private boolean isDeleted;
    private VacationStatus status;
    private Long propertyId;
    private List<TimeOffDepositResponse> timeOffDeposits;

}