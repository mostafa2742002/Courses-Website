package com.web.ecommerce.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.web.ecommerce.jwt.JwtResponse;


import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid @NotNull UserDTO userDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userDTO));
        } catch (MessagingException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification email");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody @Valid @NotNull UserDTO userDTO) {
        try {
            return ResponseEntity.ok(userService.login(userDTO));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/home/verifyemail")
    public ResponseEntity<String> verifyEmail(@RequestParam @NotNull String token) {
        return userService.verifyEmail(token);
    }

    @GetMapping("/refresh-token")
    public String postMethodName(@RequestParam @NotNull String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody @NotNull UserDTO user, @RequestParam String user_id) {
        return userService.updateProfile(user, user_id);
    }

    @PostMapping("/address")
    public ResponseEntity<String> addAddress(@RequestParam String newAddress, @RequestParam String user_id) {
        return userService.addAddress(newAddress, user_id);
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody @NotNull PasswordDTO passwordDTO) {

        return userService.updatePassword(passwordDTO.getUserId(), passwordDTO.getOldPassword(),
                passwordDTO.getNewPassword());
    }
}
