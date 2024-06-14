package com.web.CoursesQuiz.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.CoursesQuiz.constants.ServerConstants;
import com.web.CoursesQuiz.course.CourseDTO;
import com.web.CoursesQuiz.dto.ErrorResponseDto;
import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.jwt.JwtResponse;
import com.web.CoursesQuiz.lesson.LessonDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
public class UserController {

    private UserService userService;

    @Operation(summary = "Sign up a new user", description = "Sign up a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Failed to send verification email", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
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

    @Operation(summary = "Sign in a user", description = "Sign in a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User signed in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody @Valid @NotNull LoginDTO userDTO) {
        try {
            return ResponseEntity.ok(userService.login(userDTO));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @Operation(summary = "Validate The user email", description = "Validate The user email")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))

            ) })

    @GetMapping("/verifyemail")
    public ResponseEntity<String> verifyEmail(@RequestParam @NotNull String token) {
        return userService.verifyEmail(token);
    }

    @Operation(summary = "Get a new access token", description = "send a refresh token to get a new access token")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @GetMapping("/refresh-token")
    public String postMethodName(@RequestParam @NotNull String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    @Operation(summary = "update user profile", description = "update user profile by user id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody @NotNull UserDTO user, @RequestParam String user_id) {
        return userService.updateProfile(user, user_id);
    }

    @Operation(summary = "Update user password", description = "Update user password")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody @NotNull PasswordDTO passwordDTO) {

        return userService.updatePassword(passwordDTO.getUserId(), passwordDTO.getOldPassword(),
                passwordDTO.getNewPassword());
    }

    @Operation(summary = "attend a course", description = "attend a course by user id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Course attended successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @PostMapping("/course/attend")
    public ResponseEntity<CourseDTO> attendCourse(@RequestParam @NotNull String userId) {
        CourseDTO courseDTO = userService.attendCourse(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseDTO);
    }

    @Operation(summary = "complete a course", description = "complete a course and submit the solutions by user id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Course completed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @PostMapping("/course/complete")
    public ResponseEntity<String> completeCourse(@RequestBody @NotNull CourseAnswersDTO answers) {
        try {
            userService.completeCourse(answers);
            return ResponseEntity.status(HttpStatus.OK).body("Course completed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "reset a course", description = "reset a course by user id and course id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Course reset successfully"),
            @ApiResponse(responseCode = "417", description = "Failed to reset course", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @PostMapping("/course/reset")
    public ResponseEntity<ResponseDto> resetCourse(@RequestParam @NotNull String userId,
            @RequestParam @NotNull String courseId) {

        Boolean isDeleted = userService.resetCourse(userId, courseId);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(ServerConstants.STATUS_200, ServerConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(ServerConstants.STATUS_417,
                            ServerConstants.MESSAGE_417_UPDATE));
        }

    }

    @Operation(summary = "attend a lesson", description = "attend a lesson by user id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lesson attended successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })

    @PostMapping("/lesson/attend")
    public ResponseEntity<LessonDTO> attendLesson(@RequestParam @NotNull String userId) {
        LessonDTO lessonDTO = userService.attendLesson(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lessonDTO);
    }

    @Operation(summary = "complete a lesson", description = "complete a lesson and submit the solutions by user id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lesson completed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @PostMapping("/lesson/complete")
    public ResponseEntity<String> completeLesson(@RequestBody @NotNull LessonAnswersDTO answers) {
        try {
            userService.completeLesson(answers);
            return ResponseEntity.status(HttpStatus.OK).body("Course completed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "reset a lesson", description = "reset a lesson by user id and lesson id")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lesson reset successfully"),
            @ApiResponse(responseCode = "417", description = "Failed to reset lesson", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)

            )) })

    @PostMapping("/lesson/reset")
    public ResponseEntity<ResponseDto> resetLesson(@RequestParam @NotNull String userId,
            @RequestParam @NotNull String lessonId) {

        Boolean isDeleted = userService.resetLesson(userId, lessonId);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(ServerConstants.STATUS_200, ServerConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(ServerConstants.STATUS_417,
                            ServerConstants.MESSAGE_417_UPDATE));
        }

    }

}
