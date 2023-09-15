package com.example.holidayswap.domain.entity.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "all_transaction_log")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long fromID;
    private long toID;
    private long amount;
    private EnumPaymentStatus.BankCodeError resultCode;
    private String detail;
    private String createdOn;
}