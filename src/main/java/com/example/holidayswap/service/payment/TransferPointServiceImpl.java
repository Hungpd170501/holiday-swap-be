package com.example.holidayswap.service.payment;

import com.example.holidayswap.domain.dto.response.payment.TransactionTranferPointResponse;
import com.example.holidayswap.domain.dto.response.payment.TransferResponse;
import com.example.holidayswap.domain.entity.payment.AllLog;
import com.example.holidayswap.domain.entity.payment.EnumPaymentStatus;
import com.example.holidayswap.domain.entity.payment.TransactLog;
import com.example.holidayswap.domain.entity.payment.Wallet;
import com.example.holidayswap.repository.payment.AllLogRepository;
import com.example.holidayswap.repository.payment.TransactLogRepository;
import com.example.holidayswap.repository.payment.WalletRepository;
import com.example.holidayswap.service.AccountException;
import com.example.holidayswap.service.BankException;
import com.example.holidayswap.service.auth.UserService;
import com.example.holidayswap.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.holidayswap.service.payment.WalletServiceImpl.walletLocks;

@Service
public class TransferPointServiceImpl implements ITransferPointService{

    @Autowired
    private IWalletService walletService;

    @Autowired
    private ILoggingService loggingService;

    @Autowired
    private TransactLogRepository transactLogRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AllLogRepository allLogRepository;

    @Override
    @Transactional(rollbackFor = { BankException.class })
    public TransferResponse transferPoint(long from, long to, long amount) {
        Wallet fromWallet ;
        Wallet toWallet ;
        String currentDate = null;

        try {
            fromWallet = walletService.GetWalletByUserId(from);
            toWallet = walletService.GetWalletByUserId(to);
        } catch (AccountException accountException){
            loggingService.saveLog(from, to, amount, EnumPaymentStatus.BankCodeError.ID_NOT_FOUND, accountException.getMessage(), 0, 0);
            throw new BankException("Account Error");
        }
        if(walletLocks.get(fromWallet.getId()).tryLock()){

        try {


            if (fromWallet.getTotalPoint() < amount) {
                String detail = "Account " + fromWallet.getId() + " of user has id " + fromWallet.getUser().getUserId() + " does not have enough balance";
                loggingService.saveLog(from, to, amount, EnumPaymentStatus.BankCodeError.BALANCE_NOT_ENOUGH, detail,fromWallet.getTotalPoint(),toWallet.getTotalPoint());
                throw new BankException(detail);
            }

            boolean check = fromWallet.withdraw(amount);
            if (!check) {
                String detail = "Account " + fromWallet.getId() + " of user has id " + fromWallet.getUser().getUserId() + " does not have enough balance";
                loggingService.saveLog(from, to, amount, EnumPaymentStatus.BankCodeError.BALANCE_NOT_ENOUGH, detail,fromWallet.getTotalPoint(),toWallet.getTotalPoint());
                throw new BankException(detail);
            }

            toWallet.setTotalPoint((int) (toWallet.getTotalPoint() + amount));
            currentDate = Helper.getCurrentDate();
            TransactLog transactLog = new TransactLog();
            transactLog.setAmountPoint(amount);
            transactLog.setCreatedOn(currentDate);
            transactLog.setWalletFrom(fromWallet);
            transactLog.setWalletTo(toWallet);
            transactLog.setToTotalPoint(toWallet.getTotalPoint());
            transactLog.setFromTotalPoint(fromWallet.getTotalPoint());

            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);
            transactLogRepository.save(transactLog);
            loggingService.saveLog(from, to, amount, EnumPaymentStatus.BankCodeError.SUCCESS, "Success",fromWallet.getTotalPoint(),toWallet.getTotalPoint());
        } finally {
            walletLocks.get(fromWallet.getId()).unlock();
        }
        }

        return new TransferResponse(EnumPaymentStatus.BankCodeError.SUCCESS, "Success",currentDate);
    }

    @Override
    public List<TransactionTranferPointResponse> getTransactionTranferPointByUserId(Long userId){
        List<AllLog> allLogs = allLogRepository.findAllByFromIdOrToId(userId, userId);
        Helper helper = new Helper();
        List<TransactionTranferPointResponse> transactionTranferPointResponses = convertAllLogToTransactionTranferPointResponse(allLogs, userId);
        return transactionTranferPointResponses;
    }

    @Override
    public List<TransactionTranferPointResponse> convertAllLogToTransactionTranferPointResponse(List<AllLog> allLogs, Long userId) {
        return allLogs.stream().map(allLog -> {
            TransactionTranferPointResponse transactionTranferPointResponse = new TransactionTranferPointResponse();
            transactionTranferPointResponse.setId(allLog.getId());
            transactionTranferPointResponse.setFrom(userService.getUserById(allLog.getFromId()).getUsername());
            transactionTranferPointResponse.setTo(userService.getUserById(allLog.getToId()).getUsername());

            if(allLog.getFromId() == userId) {
                transactionTranferPointResponse.setTotalPoint(allLog.getFromBalance());
                transactionTranferPointResponse.setAmount("-" + allLog.getAmount());
            } else {
                transactionTranferPointResponse.setTotalPoint(allLog.getToBalance());
                transactionTranferPointResponse.setAmount("+" + allLog.getAmount());
            }

            // khi xong giao dich thi ghi lai so du cua vi cua 2 ben
            transactionTranferPointResponse.setStatus(allLog.getResultCode().name());
            transactionTranferPointResponse.setDate(allLog.getCreatedOn());
            return transactionTranferPointResponse;

        }).collect(Collectors.toList());
    }
}
