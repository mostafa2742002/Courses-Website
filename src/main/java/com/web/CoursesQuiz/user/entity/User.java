package com.web.CoursesQuiz.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.web.CoursesQuiz.user.dto.UserDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "users")
public class User extends AuditableBase implements UserDetails {

    @Id
    private String id;

    @NotNull(message = "username shouldn't be null")
    @Pattern(regexp = "^[a-zA-Z0-9 ]{3,30}$", message = "username must be between 3 and 30 characters long and can only contain letters and numbers")
    private String name;

    private String first_name;
    private String last_name;

    @Email(message = "invalid email address")
    @NotNull(message = "email shouldn't be null")
    private String email;

    private String password;

    @Pattern(regexp = "^[0-9]{11}$", message = "invalid mobile number entered ")
    @NotNull(message = "phone shouldn't be null")
    private String phone;

    @Pattern(regexp = "^[0-9]{11}$", message = "invalid mobile number entered ")
    @NotNull(message = "phone shouldn't be null")
    private String parentPhone;

    private ArrayList<CourseDate> courses = new ArrayList<>();
    private double wallet = 0.0;

    private String referralCode;
    private ArrayList<String> usedReferralCodes = new ArrayList<>();
    private ArrayList<String> userPromoCodes = new ArrayList<>();

    private ArrayList<String> notifications = new ArrayList<>();

    private String token;
    private String image;
    private boolean emailVerified;
    private String verificationToken;
    private String otp;

    public User(UserDTO userDTO) {
        this.email = userDTO.getEmail();
        this.name = userDTO.getName();
        this.password = userDTO.getPassword();
        this.phone = userDTO.getPhone();
        this.parentPhone = userDTO.getParentPhone();
        this.first_name = userDTO.getFirst_name();
        this.last_name = userDTO.getLast_name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
