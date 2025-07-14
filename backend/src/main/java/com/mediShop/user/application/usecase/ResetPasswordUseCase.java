package com.mediShop.user.application.usecase;

import com.mediShop.user.application.dto.ResetPasswordRequest;
import com.mediShop.user.domain.entity.User;
import com.mediShop.user.domain.exception.UserException;
import com.mediShop.user.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mediShop.validation.ValidationService;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordUseCase {
    private final UserRepository userRepo;
    private final ValidationService validation;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordUseCase(UserRepository userRepo,
                                ValidationService validation, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.validation = validation;
        this.passwordEncoder = passwordEncoder;
    }

    public void reset(ResetPasswordRequest req) {

        validation.validatePassword(req.newPassword);

        User user = userRepo.findByEmail(req.emailOrPhone)
                .or(() -> userRepo.findByPhone(req.emailOrPhone))
                .orElseThrow(() -> new UserException("User not found"));

        String hashedPassword = passwordEncoder.encode(req.newPassword);
        user.setPassword(hashedPassword);

        userRepo.save(user);
    }
}
