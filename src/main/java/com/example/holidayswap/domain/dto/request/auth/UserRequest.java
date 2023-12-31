package com.example.holidayswap.domain.dto.request.auth;

import com.example.holidayswap.domain.entity.auth.Gender;
import com.example.holidayswap.domain.entity.auth.UserStatus;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UserRequest {
    private MultipartFile avatar;
    private String email;
    private String password;
    private String username;
    private String fullName;
    private Gender gender;
    private LocalDate dob;
    private String phone;
    private UserStatus status;
    private Long roleId;
}
