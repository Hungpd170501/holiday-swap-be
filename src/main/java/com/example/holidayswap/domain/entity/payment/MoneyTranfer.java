package com.example.holidayswap.domain.entity.payment;

import com.example.holidayswap.domain.entity.auth.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "money_transfer")
public class MoneyTranfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "money_transfer_id"
    )
    private Long id;
    @JsonIgnore

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "order_infor",nullable = false)
    private String orderInfor;

    @Column(name = "amount",nullable = false)
    private double amount;

    @Column (name = "amount_coint_deposit")
    private int amountCoinDeposit;

    @Column(name = "payment_date",nullable = false)
    private String paymentDate;

    @Column(name = "totalPoint")
    private Double totalPoint;

    @Column(name = "status",nullable = false)
    private EnumPaymentStatus.StatusMoneyTranfer status;

}
