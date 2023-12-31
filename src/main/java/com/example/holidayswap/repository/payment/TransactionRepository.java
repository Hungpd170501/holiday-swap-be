package com.example.holidayswap.repository.payment;

import com.example.holidayswap.domain.entity.auth.User;
import com.example.holidayswap.domain.entity.payment.EnumPaymentStatus;
import com.example.holidayswap.domain.entity.payment.MoneyTranfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<MoneyTranfer, Long> {

    List<MoneyTranfer> findByUserOrderByIdAsc(User user);

    List<MoneyTranfer> findAllByPaymentDateContainingAndStatus(String paymentDate, EnumPaymentStatus.StatusMoneyTranfer status);

    List<MoneyTranfer> findAllByPaymentDateBetween(String startDate, String endDate);
    List<MoneyTranfer> findAllByPaymentDateBetweenAndStatus(String startDate, String endDate,EnumPaymentStatus.StatusMoneyTranfer status);
}
