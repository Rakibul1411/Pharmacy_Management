package com.mediShop.user.application.usecase;

import com.mediShop.user.application.dto.OtpVerificationRequest;
import com.mediShop.user.domain.entity.User;
import com.mediShop.user.domain.exception.UserException;
import com.mediShop.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class OtpVerificationUseCase {
    private final UserRepository userRepo;

    public OtpVerificationUseCase(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void verify(OtpVerificationRequest req) {
        User user = userRepo.findByOtp(req.otp)
                .orElseThrow(() -> new UserException("Invalid OTP"));

        user.verifyOtp(req.otp);
        userRepo.save(user);
    }
}
