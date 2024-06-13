package com.web.ecommerce.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.ecommerce.email.EmailService;
import com.web.ecommerce.jwt.JwtResponse;
import com.web.ecommerce.jwt.JwtService;

import jakarta.mail.MessagingException;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JwtService jwtService, UserDetailsServiceImpl userDetailsService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found");
        return user;
    }

    public String register(@NonNull UserDTO userDTO) throws MessagingException, InterruptedException {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new IllegalArgumentException("User already exists");
        }
        userDTO.setEmail(userDTO.getEmail().toLowerCase());
        User user = new User(userDTO);
        String verificationToken = jwtService.generateToken(user);

        user.setEmailVerified(false);
        user.setVerificationToken(verificationToken);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        String subject = "Verify Your Email";

        // if we use render site then use this
        String body = "Click the link to verify your email:https://ecommerce-j0hf.onrender.com/home/verifyemail?token="
                + verificationToken;

        // if we use localhost then use this
        // String body = "Click the link to verify your
        // email:http://localhost:8080/home/verifyemail?token="
        // + verificationToken;
        emailService.sendEmail(savedUser.getEmail(), subject, body);

        return "the user added successfully go to your email to verify your email";
    }

    public JwtResponse login(@NonNull UserDTO userDTO) {
        userDTO.setEmail(userDTO.getEmail().toLowerCase());
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user != null && bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            return new JwtResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user), user);
        }
        throw new IllegalArgumentException("Invalid credentials");
    }

    public User findUserByEmail(String email) {
        if (userRepository.findByEmail(email) == null) {
            throw new IllegalArgumentException("User not found");
        }
        return userRepository.findByEmail(email);
    }

    public void saveProfileImage(String email, String image) {
        User user = findUserByEmail(email);
        user.setImage(image);
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername());
    }

    public String getCurrentUserProfilePicture() {
        User currentUser = getCurrentUser();
        return currentUser.getImage();
    }

    public String getCurrentUserName() {
        User currentUser = getCurrentUser();
        return currentUser.getName();
    }

    public String getCurrentUserEmail() {
        User currentUser = getCurrentUser();
        return currentUser.getEmail();
    }

    public String refreshToken(String refreshToken) {
        String email = jwtService.extractUserName(refreshToken);
        if (email == null) {
            throw new RuntimeException("Invalid Token");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!jwtService.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("expired Token or Invalid");
        }

        return jwtService.generateToken(userDetails);
    }

    public ResponseEntity<String> addStar(String userId, String productId) {
        try {
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (user.getStar() == null) {
                user.setStar(new ArrayList<>());
            }

            List<String> stars = new ArrayList<>(user.getStar());

            if (stars.contains(productId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product already in stars");
            }

            stars.add(productId);
            user.setStar(stars);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body("product added to stars successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error addingproduct to stars");
        }
    }

    public ResponseEntity<String> removeStar(String userId, String productId) {
        try {
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (user.getStar() == null || user.getStar().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Noproducts in stars to remove");
            }

            List<String> stars = new ArrayList<>(user.getStar());

            if (!stars.contains(productId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product not found in stars");
            }

            stars.remove(productId);
            user.setStar(stars);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body("product removed from stars successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removingproduct from stars");
        }
    }

    public ResponseEntity<String> addCart(String userId, String productId) {
        try {
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (user.getCart() == null) {
                user.setCart(new ArrayList<>());
            }

            List<String> carts = new ArrayList<>(user.getCart());

            if (carts.contains(productId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product already in the cart");
            }

            carts.add(productId);
            user.setCart(carts);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body("product added to the cart successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error addingproduct to the cart");
        }
    }

    public ResponseEntity<String> removeCart(String userId, String productId) {
        try {
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (user.getCart() == null || user.getCart().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Noproducts in the cart to remove");
            }

            List<String> carts = new ArrayList<>(user.getCart());

            if (!carts.contains(productId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product not found in the cart");
            }

            carts.remove(productId);
            user.setCart(carts);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body("product removed from the cart successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removingproduct from the cart");
        }
    }

    public ResponseEntity<String> verifyEmail(String token) {
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user1 = user;
        if (user1.getVerificationToken().equals(token)) {
            user1.setEmailVerified(true);
            user1.setVerificationToken(null);
            userRepository.save(user1);

            return ResponseEntity.ok("Email verified successfully");

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Token");
        }
    }

    public ResponseEntity<String> updateProfile(UserDTO user, String user_id) {

        User user1 = userRepository.findById(user_id).orElse(null);

        if (user1 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        user1.setName(user.getName());
        user1.setPhone(user.getPhone());
        user1.setEmail(user.getEmail());
        userRepository.save(user1);

        return ResponseEntity.ok("Profile updated successfully");
    }

    public ResponseEntity<String> updatePassword(String user_id, String oldPassword, String newPassword) {
        User user = userRepository.findById(user_id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }

    public ResponseEntity<String> addAddress(String NewAddress, String user_id) {
        User user = userRepository.findById(user_id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (user.getAddress() == null) {
            user.setAddress(new ArrayList<>());
        }

        user.getAddress().add(NewAddress);

        userRepository.save(user);

        return ResponseEntity.ok("Address added successfully");

    }
}
