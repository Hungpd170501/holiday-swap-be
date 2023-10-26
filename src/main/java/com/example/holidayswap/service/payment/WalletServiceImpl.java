package com.example.holidayswap.service.payment;

import com.example.holidayswap.domain.entity.auth.User;
import com.example.holidayswap.domain.entity.payment.Wallet;
import com.example.holidayswap.repository.auth.UserRepository;
import com.example.holidayswap.repository.payment.WalletRepository;
import com.example.holidayswap.service.BankException;
import com.example.holidayswap.service.firebase.INotificationUserService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements IWalletService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserRepository userRepository;

    private final INotificationUserService notificationUserService;

    @Override
    @Transactional
    public boolean TopUpWallet(Long userId, int amount) {
        Wallet userWallet = walletRepository.findByUser(userRepository.findById(userId).orElse(null));
        if (userWallet == null) userWallet = CreateWallet(userId);
        if (userWallet == null) throw new BankException("Wallet not found");

        userWallet.setTotalPoint(userWallet.getTotalPoint() + amount);

        walletRepository.save(userWallet);

        try {
            notificationUserService.CreateNotificationByUserId(userId,"Deposit from VN Pay","+ " + amount,"/wallet");
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public Wallet CreateWallet(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) throw new BankException("User not found");
        Wallet userWallet = user.getWallet();

        if (userWallet == null) {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setTotalPoint(0D);
            wallet.setStatus(true);
            walletRepository.save(wallet);
            user.setWallet(wallet);
            return wallet;
        }
        return userWallet;
    }

    @Override
    public Wallet GetWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUser(userRepository.findById(userId).orElse(null));
        return wallet;
    }
}
