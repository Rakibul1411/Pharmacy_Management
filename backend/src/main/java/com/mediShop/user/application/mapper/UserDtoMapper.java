package com.mediShop.user.application.mapper;

import com.mediShop.user.application.dto.UserResponse;
import com.mediShop.user.domain.entity.User;

public class UserDtoMapper {
    public static UserResponse toResponse(User user) {

        UserResponse userResponse = new UserResponse();

        userResponse.id        = user.getId();
        userResponse.name      = user.getName();
        userResponse.email     = user.getEmail();
        userResponse.phone     = user.getPhone();
        userResponse.role      = user.getRole().name();
        userResponse.verified  = user.isVerified();
        userResponse.createdAt = user.getCreatedAt();
        userResponse.updatedAt = user.getUpdatedAt();
        return userResponse;
    }
}
