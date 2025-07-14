package com.mediShop.user.application.usecase;

import com.mediShop.user.application.dto.*;
import com.mediShop.user.application.mapper.UserDtoMapper;
import com.mediShop.user.domain.entity.User;
import com.mediShop.user.domain.exception.UserException;
import com.mediShop.user.domain.repository.UserRepository;
import com.mediShop.user.domain.valueobject.Role;
import com.mediShop.validation.ValidationService;
import com.mediShop.user.application.service.EmailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RegisterUserUseCase {
    private final UserRepository userRepo;
    private final ValidationService validation;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    public RegisterUserUseCase(UserRepository userRepo, ValidationService validation, EmailService emailService, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.validation = validation;
        this.emailService = emailService;
        this.encoder = encoder;
    }

    public UserResponse register(UserRegistrationRequest req) {
        validation.validateEmail(req.email);
        validation.validatePhoneNumber(req.phone);
        validation.validatePassword(req.password);

        String normalizedPhone = validation.normalizePhoneNumber(req.phone);

        String hashedPassword = encoder.encode(req.password);

        User user = User.create(req.name, req.email, normalizedPhone, hashedPassword, req.role);

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new UserException("Email already in use");
        }
        if (userRepo.findByPhone(user.getPhone()).isPresent()) {
            throw new UserException("Phone already in use");
        }

        user.generateOtp(10);

        User saveUser = userRepo.save(user);

        emailService.sendOtpEmail(saveUser.getEmail(), saveUser.getOtp());

        return UserDtoMapper.toResponse(saveUser);
    }
}
