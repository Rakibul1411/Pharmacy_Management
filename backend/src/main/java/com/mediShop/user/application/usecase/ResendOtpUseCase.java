package com.mediShop.user.application.usecase;

import com.mediShop.user.application.dto.ResendOtpRequest;
import com.mediShop.user.application.service.EmailService;
import com.mediShop.user.domain.entity.User;
import com.mediShop.user.domain.exception.UserException;
import com.mediShop.user.domain.repository.UserRepository;
import com.mediShop.validation.ValidationService;
import org.springframework.stereotype.Service;

@Service
public class ResendOtpUseCase {
    private final UserRepository userRepo;
    private final ValidationService validation;
    private final EmailService emailService;

    public ResendOtpUseCase(UserRepository userRepo,
                           ValidationService validation,
                           EmailService emailService) {
        this.userRepo = userRepo;
        this.validation = validation;
        this.emailService = emailService;
    }

    public void resend(ResendOtpRequest req) {
        validation.validateEmailOrPhone(req.emailOrPhone);

        // Find user by email or phone
        User user = userRepo.findByEmail(req.emailOrPhone)
                .or(() -> userRepo.findByPhone(req.emailOrPhone))
                .orElseThrow(() -> new UserException("User not found"));

        // Check if user has email for sending OTP
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserException("User does not have an email address to receive OTP");
        }

        // Generate new OTP with 10 minutes validity
        user.generateOtp(10);
        userRepo.save(user);

        // Send the new OTP via email
        emailService.sendOtpEmail(user.getEmail(), user.getOtp());
    }
}
