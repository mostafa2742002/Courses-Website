package com.web.CoursesQuiz.user;

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

import com.web.CoursesQuiz.course.Course;
import com.web.CoursesQuiz.course.CourseController;
import com.web.CoursesQuiz.course.CourseDTO;
import com.web.CoursesQuiz.course.CourseRepository;
import com.web.CoursesQuiz.course.CourseService;
import com.web.CoursesQuiz.course.SolvedCourse;
import com.web.CoursesQuiz.course.SolvedCourseRepository;
import com.web.CoursesQuiz.email.EmailService;
import com.web.CoursesQuiz.jwt.JwtResponse;
import com.web.CoursesQuiz.jwt.JwtService;
import com.web.CoursesQuiz.lesson.LessonDTO;
import com.web.CoursesQuiz.lesson.LessonService;
import com.web.CoursesQuiz.lesson.SolvedLesson;
import com.web.CoursesQuiz.lesson.SolvedLessonRepository;

import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final EmailService emailService;
    private final SolvedCourseRepository solvedCourseRepository;
    private final SolvedLessonRepository solvedLessonRepository;
    private final CourseService courseService;
    private final LessonService lessonService;
    private final CourseRepository courseRepository;

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
        // String body = "Click the link to verify your
        // email:https://ecommerce-j0hf.onrender.com/home/verifyemail?token="
        // + verificationToken;

        // if we use localhost then use this
        String body = "Click the link to verify your email:http://localhost:8080/verifyemail?token="
                + verificationToken;
        emailService.sendEmail(savedUser.getEmail(), subject, body);

        return "the user added successfully go to your email to verify your email";
    }

    public JwtResponse login(@NonNull LoginDTO userDTO) {
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

    public CourseDTO attendCourse(@NotNull String userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        SolvedCourse solvedCourse = solvedCourseRepository.findByUserId(userId);
        CourseDTO courseDTO = courseService.getCourse(userId);

        if (solvedCourse != null)
            courseDTO.setFinalQuiz(solvedCourse.getFinalQuiz());
        return courseDTO;
    }

    public void completeCourse(@NotNull CourseAnswersDTO answers) {
        if (userRepository.findById(answers.getUserId()).isEmpty())
            throw new IllegalArgumentException("User not found");

        SolvedCourse solvedCourse = new SolvedCourse();
        solvedCourse.setUserId(answers.getUserId());
        solvedCourse.setFinalQuiz(answers.getQuestionsAnswers());
        solvedCourse.setCourseId(answers.getCourseId());

        if (solvedCourseRepository.findById(answers.getUserId()).isPresent())
            solvedCourseRepository.deleteById(answers.getUserId());

        solvedCourseRepository.save(solvedCourse);
    }

    public Boolean resetCourse(@NotNull String userId, @NotNull String courseId) {
        Boolean isDeleted = false;
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        // delete solved course by the userid and courseid together
        if (solvedCourseRepository.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            long deletedCount = solvedCourseRepository.deleteByUserIdAndCourseId(userId, courseId);
            isDeleted = deletedCount > 0;
        }

        return isDeleted;
    }

    public LessonDTO attendLesson(@NotNull String userId) {

        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        SolvedLesson solvedLesson = solvedLessonRepository.findByUserId(userId);
        LessonDTO lessonDTO = lessonService.getLesson(userId);

        if (solvedLesson != null)
            lessonDTO.setLessonQuestions(solvedLesson.getLessonQuestions());

        return lessonDTO;
    }

    public void completeLesson(@NotNull LessonAnswersDTO answers) {
        if (userRepository.findById(answers.getUserId()).isEmpty())
            throw new IllegalArgumentException("User not found");

        SolvedLesson solvedLesson = new SolvedLesson();
        solvedLesson.setUserId(answers.getUserId());
        solvedLesson.setLessonQuestions(answers.getQuestionsAnswers());
        solvedLesson.setLessonId(answers.getLessonId());

        if (solvedLessonRepository.findById(answers.getUserId()).isPresent())
            solvedLessonRepository.deleteById(answers.getUserId());

        solvedLessonRepository.save(solvedLesson);
    }

    public Boolean resetLesson(@NotNull String userId, @NotNull String lessonId) {
        Boolean isDeleted = false;
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        // delete solved lesson by the userid and lessonid together
        if (solvedLessonRepository.findByUserIdAndLessonId(userId, lessonId).isPresent()) {
            long deletedCount = solvedLessonRepository.deleteByUserIdAndLessonId(userId, lessonId);
            isDeleted = deletedCount > 0;
        }

        return isDeleted;
    }

    public void enrollCourse(@NotNull String userId, @NotNull String courseId) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");
        if (courseRepository.findById(courseId).isEmpty())
            throw new IllegalArgumentException("Course not found");

        User user = userRepository.findById(userId).get();
        if (user.getCourses().contains(courseId))
            throw new IllegalArgumentException("User already enrolled in this course");

        user.getCourses().add(courseId);
        userRepository.save(user);
    }

}
