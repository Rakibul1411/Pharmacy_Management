package com.mediShop.user.application.dto;

import java.time.LocalDateTime;

public class UserResponse {
    public Integer id;
    public String name;
    public String email;
    public String phone;
    public String role;
    public boolean verified;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
