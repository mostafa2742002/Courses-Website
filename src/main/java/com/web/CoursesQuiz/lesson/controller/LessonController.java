package com.web.CoursesQuiz.lesson.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.CoursesQuiz.constants.ServerConstants;
import com.web.CoursesQuiz.course.entity.PageResponse;
import com.web.CoursesQuiz.dto.ErrorResponseDto;
import com.web.CoursesQuiz.dto.ResponseDto;
import com.web.CoursesQuiz.lesson.dto.LessonDTO;
import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.lesson.service.LessonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
public class LessonController {

        private final LessonService lessonService;

        @Operation(summary = "Create Lesson REST API", description = "REST API to create new Lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @PostMapping("/lesson")
        public ResponseEntity<ResponseDto> AddLesson(@RequestBody LessonDTO lessonDTO,
                        @RequestParam @NotNull String courseId, @RequestParam @NotNull String chapterId) {
                lessonService.addLesson(lessonDTO, courseId, chapterId);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(new ResponseDto(ServerConstants.STATUS_201, ServerConstants.MESSAGE_201));
        }

        @Operation(summary = "Fetch Lesson Details REST API", description = "REST API to fetch Lesson &  Lesson details based on a Lesson ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @GetMapping("/lesson")
        public ResponseEntity<LessonDTO> getLesson(@RequestParam @NotNull String lessonId,
                        @RequestParam @NotNull String level) {
                LessonDTO lessonDTO = lessonService.getLesson(lessonId, level);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(lessonDTO);
        }

        @Operation(summary = "Update Lesson Details REST API", description = "REST API to update Lesson &  Lesson details based on a Lesson ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "417", description = "Expectation Failed"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @PutMapping("/lesson")
        public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody LessonDTO lessonDTO) {
                boolean isUpdated = lessonService.updateLesson(lessonDTO);
                if (isUpdated) {
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

        @Operation(summary = "Delete Lesson REST API", description = "REST API to delete Lesson based on a Lesson ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "417", description = "Expectation Failed"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @DeleteMapping("/lesson")
        public ResponseEntity<ResponseDto> deleteLesson(@RequestParam @NotNull String lessonId) {
                boolean isDeleted = lessonService.deleteLesson(lessonId);
                if (isDeleted) {
                        return ResponseEntity
                                        .status(HttpStatus.OK)
                                        .body(new ResponseDto(ServerConstants.STATUS_200, ServerConstants.MESSAGE_200));
                } else {
                        return ResponseEntity
                                        .status(HttpStatus.EXPECTATION_FAILED)
                                        .body(new ResponseDto(ServerConstants.STATUS_417,
                                                        ServerConstants.MESSAGE_417_DELETE));
                }
        }

        @Operation(summary = "Fetch all Lessons REST API", description = "REST API to fetch all Lessons")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @GetMapping("/lessons")
        public ResponseEntity<PageResponse<Lesson>> findAllLessons(
                        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
                        @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
                PageResponse<Lesson> Lessons = lessonService.findAllLessons(page, size);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(Lessons);
        }

        @Operation(summary = "Add A New Question REST API", description = "REST API to Add a new Question to a Lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @PostMapping("/lesson/question")
        public ResponseEntity<ResponseDto> addQuestion(@RequestBody @NotNull Question question,
                        @RequestParam @NotNull String lessonId) {
                lessonService.addQuestion(question, lessonId);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(new ResponseDto(ServerConstants.STATUS_201, ServerConstants.MESSAGE_201));
        }

        @Operation(summary = "Fetch all Questions REST API", description = "REST API to fetch all Questions of a Lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @GetMapping("/lesson/questions")
        public ResponseEntity<List<Question>> getAllQuestions(@RequestParam @NotNull String lessonId,
                        @RequestParam @NotNull String level) {
                List<Question> questions = lessonService.getAllQuestions(lessonId, level);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(questions);
        }

        @Operation(summary = "Fetch all Free Questions REST API", description = "REST API to fetch all Free Questions of a Lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @GetMapping("/lesson/questions/free")
        public ResponseEntity<List<Question>> getAllFreeQuestions(@RequestParam @NotNull String lessonId) {
                List<Question> questions = lessonService.getAllFreeQuestions(lessonId);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(questions);
        }

        @Operation(summary = "Delete Question REST API", description = "REST API to delete a Question from a Lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "417", description = "Expectation Failed"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @DeleteMapping("/lesson/question")
        public ResponseEntity<ResponseDto> deleteQuestion(@RequestParam @NotNull String questionId) {
                boolean isDeleted = lessonService.deleteQuestion(questionId);
                if (isDeleted) {
                        return ResponseEntity
                                        .status(HttpStatus.OK)
                                        .body(new ResponseDto(ServerConstants.STATUS_200, ServerConstants.MESSAGE_200));
                } else {
                        return ResponseEntity
                                        .status(HttpStatus.EXPECTATION_FAILED)
                                        .body(new ResponseDto(ServerConstants.STATUS_417,
                                                        ServerConstants.MESSAGE_417_DELETE));
                }
        }

        @Operation(summary = "Update Question REST API", description = "REST API to update a Question from a Lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
                        @ApiResponse(responseCode = "417", description = "Expectation Failed"),
                        @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        })
        @PutMapping("/lesson/question")
        public ResponseEntity<ResponseDto> updateQuestion(@RequestBody @NotNull Question question) {
                boolean isUpdated = lessonService.updateQuestion(question);
                if (isUpdated) {
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
