package com.mediShop.user.application.usecase;

import com.mediShop.user.application.dto.ForgotPasswordRequest;
import com.mediShop.user.application.service.EmailService;
import com.mediShop.user.domain.entity.User;
import com.mediShop.user.domain.exception.UserException;
import com.mediShop.user.domain.repository.UserRepository;
import com.mediShop.validation.ValidationService;
import org.springframework.stereotype.Service;

@Service
public class InitiatePasswordResetUseCase {
    private final UserRepository userRepo;
    private final ValidationService validation;
    private final EmailService emailService;

    public InitiatePasswordResetUseCase(UserRepository userRepo,
                                        ValidationService validation,
                                        EmailService emailService) {
        this.userRepo = userRepo;
        this.validation = validation;
        this.emailService = emailService;
    }

    public void initiate(ForgotPasswordRequest req) {
        validation.validateEmailOrPhone(req.emailOrPhone);
        User user = userRepo.findByEmail(req.emailOrPhone)
                .or(() -> userRepo.findByPhone(req.emailOrPhone))
                .orElseThrow(() -> new UserException("User not found"));

        user.generateOtp(10);
        userRepo.save(user);

        if (user.getEmail() != null) {
            emailService.sendOtpEmail(user.getEmail(), user.getOtp());
        }
    }
}