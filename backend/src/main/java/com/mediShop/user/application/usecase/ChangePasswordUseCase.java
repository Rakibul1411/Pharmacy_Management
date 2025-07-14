package com.mediShop.user.application.usecase;

import com.mediShop.user.application.dto.ChangePasswordRequest;
import com.mediShop.user.domain.entity.User;
import com.mediShop.user.domain.exception.UserException;
import com.mediShop.user.domain.repository.UserRepository;
import com.mediShop.validation.ValidationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordUseCase {
    private final UserRepository userRepo;
    private final ValidationService validation;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCase(UserRepository userRepo,
                                 ValidationService validation,
                                 PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.validation = validation;
        this.passwordEncoder = passwordEncoder;
    }

    public void change(ChangePasswordRequest req) {

        if (!req.newPassword.equals(req.confirmPassword)) {
            throw new UserException("New password and confirmation do not match");
        }

        validation.validatePassword(req.newPassword);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        System.out.println("Current user email:..... " + currentEmail);

        User user = userRepo.findByEmail(currentEmail)
                .orElseThrow(() -> new UserException("User not found"));

        if (!passwordEncoder.matches(req.oldPassword, user.getPassword())) {
            throw new UserException("Current password is incorrect");
        }

        user.ensureNewPasswordIsDifferent(req.newPassword, passwordEncoder::matches);

        String hashedPassword = passwordEncoder.encode(req.newPassword);
        user.setPassword(hashedPassword);

        userRepo.save(user);
    }
}
