package com.example.holidayswap.controller.property.vacation;

import com.example.holidayswap.domain.dto.request.property.vacation.TimeOffDepositRequest;
import com.example.holidayswap.domain.dto.response.property.vacation.TimeOffDepositResponse;
import com.example.holidayswap.service.property.vacation.TimeOffDepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/time-off-deposits")
public class TimeOffDepositsController {
    private final TimeOffDepositService timeOffDepositService;

    @GetMapping("/vacation-units")
    public ResponseEntity<Page<TimeOffDepositResponse>> getAllByVacationUnitId(
            @RequestParam(value = "vacationUnitId") Long vacationUnitId,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        var dtoResponses = timeOffDepositService.getAllByVacationUnitId(vacationUnitId, pageable);
        return ResponseEntity.ok(dtoResponses);
    }

    @GetMapping("/properties")
    public ResponseEntity<Page<TimeOffDepositResponse>> getAllByPropertyId(
            @RequestParam("vacationUnitId") Long vacationUnitId,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        var dtoResponses = timeOffDepositService.getAllByPropertyId(vacationUnitId, pageable);
        return ResponseEntity.ok(dtoResponses);
    }

    @GetMapping("/resort")
    public ResponseEntity<Page<TimeOffDepositResponse>> getAllByResortId(
            @RequestParam Long vacationUnitId,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        var dtoResponses = timeOffDepositService.getAllByResortId(vacationUnitId, pageable);
        return ResponseEntity.ok(dtoResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeOffDepositResponse> get(
            @PathVariable("id") Long id) {
        var dtoResponse = timeOffDepositService.get(id);
        return ResponseEntity.ok(dtoResponse);
    }

    @PostMapping("/{vacationUnitId}")
    public ResponseEntity<TimeOffDepositResponse> create(
            @PathVariable("vacationUnitId") Long vacationUnitId,
            @RequestBody TimeOffDepositRequest dtoRequest) {
        var dtoResponse = timeOffDepositService.create(vacationUnitId, dtoRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dtoResponse.getId())
                .toUri();
        return ResponseEntity.created(location).body(dtoResponse);
    }

    @PutMapping(value = "/{timeOffDepositId}")
    public ResponseEntity<Void> update(@PathVariable("timeOffDepositId") Long timeOffDepositId,
                                       @RequestBody TimeOffDepositRequest dtoRequest) {
        timeOffDepositService.update(timeOffDepositId, dtoRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(timeOffDepositId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timeOffDepositService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
