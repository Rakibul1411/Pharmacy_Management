package com.mediShop.user.application.usecase;

import com.mediShop.user.application.dto.UserLoginRequest;
import com.mediShop.user.application.dto.UserResponse;
import com.mediShop.user.application.mapper.UserDtoMapper;
import com.mediShop.user.domain.entity.User;
import com.mediShop.user.domain.exception.UserException;
import com.mediShop.user.domain.repository.UserRepository;
import com.mediShop.validation.ValidationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserUseCase {
    private final UserRepository userRepo;
    private final ValidationService validation;
    private final PasswordEncoder encoder;

    public LoginUserUseCase(UserRepository userRepo, ValidationService validation, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.validation = validation;
        this.encoder = encoder;
    }

    public UserResponse login(UserLoginRequest req) {
        validation.validateEmailOrPhone(req.emailOrPhone);
        User user = userRepo.findByEmail(req.emailOrPhone)
                .or(() -> userRepo.findByPhone(req.emailOrPhone))
                .orElseThrow(() -> new UserException("User not found"));

        if (!encoder.matches(req.password, user.getPassword())) {
            throw new UserException("Invalid credentials");
        }

        return UserDtoMapper.toResponse(user);
    }
}