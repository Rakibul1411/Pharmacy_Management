package com.mediShop.user.domain.repository;

import com.mediShop.user.domain.entity.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByOtp(String otp);
    void delete(User user);
}
