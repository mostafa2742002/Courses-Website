package com.web.CoursesQuiz.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import com.web.CoursesQuiz.chapter.repo.ChapterRepository;
import com.web.CoursesQuiz.course.entity.Course;
import com.web.CoursesQuiz.course.entity.SolvedCourse;
import com.web.CoursesQuiz.course.repo.CourseRepository;
import com.web.CoursesQuiz.course.repo.SolvedCourseRepository;
import com.web.CoursesQuiz.course.service.CourseService;
import com.web.CoursesQuiz.email.EmailService;
import com.web.CoursesQuiz.jwt.JwtResponse;
import com.web.CoursesQuiz.jwt.JwtService;

import com.web.CoursesQuiz.lesson.entity.Answer;
import com.web.CoursesQuiz.lesson.entity.Lesson;
import com.web.CoursesQuiz.lesson.entity.Question;
import com.web.CoursesQuiz.lesson.entity.SolvedLesson;
import com.web.CoursesQuiz.lesson.repo.LessonRepository;
import com.web.CoursesQuiz.lesson.repo.SolvedLessonRepository;
import com.web.CoursesQuiz.lesson.service.LessonService;
import com.web.CoursesQuiz.user.dto.CourseAnswersDTO;
import com.web.CoursesQuiz.user.dto.CourseInfo;
import com.web.CoursesQuiz.user.dto.CourseInfo.CourseQuestions;
import com.web.CoursesQuiz.user.dto.LoginDTO;
import com.web.CoursesQuiz.user.dto.UserDTO;
import com.web.CoursesQuiz.user.entity.AttendCourse;
import com.web.CoursesQuiz.user.entity.AttendLesson;
import com.web.CoursesQuiz.user.entity.CourseDate;
import com.web.CoursesQuiz.user.entity.DiscountValue;
import com.web.CoursesQuiz.user.entity.PromoCode;
import com.web.CoursesQuiz.user.entity.ReferralCode;
import com.web.CoursesQuiz.user.entity.User;
import com.web.CoursesQuiz.user.repo.DiscountRepository;
import com.web.CoursesQuiz.user.repo.PromoCodeRepository;
import com.web.CoursesQuiz.user.repo.ReferralCodeRepository;
import com.web.CoursesQuiz.user.repo.UserRepository;

import com.web.CoursesQuiz.user.dto.LessonInfo;
import com.web.CoursesQuiz.user.dto.LessonInfo.LessonQuestions;

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
    private final ReferralCodeRepository referralCodeRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final DiscountRepository discountRepository;

    // get the discountValue from the application.properties
    private final String discountValue = System.getenv("DISCOUNT_VALUE");

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
        savedUser.setReferralCode(createReferralCode(user.getId()));
        userRepository.save(savedUser);

        String subject = "Verify Your Email";

        // if we use render site then use this
        String body = "Click the link to verify your email:https://courses-website-q0gf.onrender.com/api/verifyemail?token="
                + verificationToken;

        // if we use localhost then use this
        // String body = "Click the link to verify your
        // email:http://localhost:8080/api/verifyemail?token="
        // + verificationToken;
        emailService.sendEmail(savedUser.getEmail(), subject, body);

        return "the user added successfully go to your email to verify your email";
    }

    public JwtResponse login(@NonNull LoginDTO userDTO) {
        userDTO.setEmail(userDTO.getEmail().toLowerCase());
        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user != null && bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {

            if (user.isEmailVerified() == false)
                throw new IllegalArgumentException("Email not verified");

            ArrayList<String> notifications = user.getNotifications();
            for (CourseDate courseDate : user.getCourses()) {
                if (courseDate.getExpiryDate().isBefore(LocalDate.now())) {
                    notifications.add("Your course " + courseService.getCourseName(courseDate.getCourseId())
                            + " has expired. Please renew it to continue learning.");
                }

                // if any course will expire after 10 days
                if (courseDate.getExpiryDate().minusDays(10).isBefore(LocalDate.now())) {
                    notifications.add("Your course " + courseService.getCourseName(courseDate.getCourseId())
                            + " will expire in 10 days. Please renew it to continue learning.");
                }
            }

            // delete the expired courses
            user.getCourses().removeIf(courseDate -> courseDate.getExpiryDate().isBefore(LocalDate.now()));

            user.setNotifications(notifications);

            userRepository.save(user);

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
        if (user.getName() != null)
            user1.setName(user.getName());
        if (user.getPhone() != null)
            user1.setPhone(user.getPhone());
        if (user.getEmail() != null)
            user1.setEmail(user.getEmail());
        if (user.getImage() != null)
            user1.setImage(user.getImage());

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

    public Boolean attendCourse(@NotNull String userId, @NotNull String courseId) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        if (courseRepository.findById(courseId).isEmpty())
            throw new IllegalArgumentException("Course not found");

        SolvedCourse solvedCourse = solvedCourseRepository.findByUserIdAndCourseId(userId, courseId);

        if (solvedCourse == null) {
            SolvedCourse new_SolvedCourse = new SolvedCourse();
            new_SolvedCourse.setUserId(userId);
            new_SolvedCourse.setCourseId(courseId);
            ArrayList<Question> questions = courseService.getAllQuestions(courseId);

            for (Question question : questions) {
                Answer answer = new Answer();
                answer.setQuestionId(question.getId());
                answer.setQuestion(question.getQuestion());
                answer.setCorrectAnswer(question.getCorrectAnswer());
                answer.setUserAnswer("");
                answer.setExplaination(question.getExplanation());
                answer.setImage(question.getImage());
                answer.setIsCorrect(false);
                answer.setOptions(question.getOptions());
                new_SolvedCourse.getFinalQuiz().add(answer);
            }
            solvedCourseRepository.save(new_SolvedCourse);
            return false;
        }
        // we need to update the solved course if a new question is added to the course
        // or a question is deleted

        ArrayList<Question> questions = courseService.getAllQuestions(courseId);
        for (Question question : questions) {
            Optional<Answer> optionalAnswer = solvedCourse.getFinalQuiz().stream()
                    .filter(a -> a.getQuestionId().equals(question.getId())).findFirst();
            if (optionalAnswer.isEmpty()) {
                Answer answer = new Answer();
                answer.setQuestionId(question.getId());
                answer.setQuestion(question.getQuestion());
                answer.setCorrectAnswer(question.getCorrectAnswer());
                answer.setUserAnswer("");
                answer.setExplaination(question.getExplanation());
                answer.setImage(question.getImage());
                answer.setIsCorrect(false);
                answer.setOptions(question.getOptions());
                solvedCourse.getFinalQuiz().add(answer);
            }
        }

        // now we want to delete the quetions that are not in the course
        solvedCourse.getFinalQuiz().removeIf(answer -> questions.stream()
                .noneMatch(question -> question.getId().equals(answer.getQuestionId())));

        solvedCourseRepository.save(solvedCourse);

        Boolean firstTime = solvedCourse.getFirstTime();

        return !firstTime;
    }

    public void completeCourse(@NotNull CourseAnswersDTO answers) {
        // System.out.println("11111111111111111111111");
        if (userRepository.findById(answers.getUserId()).isEmpty())
            throw new IllegalArgumentException("User not found");

        if (courseRepository.findById(answers.getCourseId()).isEmpty())
            throw new IllegalArgumentException("Course not found");
        // System.out.println(answers);
        // System.out.println(answers.getCourseId());
        // System.out.println(answers.getUserId());

        SolvedCourse solvedCourse = solvedCourseRepository.findByUserIdAndCourseId(answers.getUserId(),
                answers.getCourseId());
        // System.out.println(solvedCourse);
        if (solvedCourse == null)
            throw new IllegalArgumentException("Solution not found");

        SolvedCourse solvedCourse1 = solvedCourse;
        for (Answer answer : answers.getQuestionsAnswers()) {
            Optional<Answer> optionalAnswer = solvedCourse1.getFinalQuiz().stream()
                    .filter(a -> a.getQuestionId().equals(answer.getQuestionId()))
                    .findFirst();
            if (optionalAnswer.isPresent()) {
                Answer answer1 = optionalAnswer.get();
                answer1.setUserAnswer(answer.getUserAnswer());
                answer1.setIsCorrect(answer.getIsCorrect());
            } else {
                throw new IllegalArgumentException("Question not found in final quiz");
            }
        }

        int grade = 0;
        for (Answer answer : solvedCourse1.getFinalQuiz()) {
            if (answer.getIsCorrect())
                grade++;
        }

        solvedCourse1.setGrade(grade);
        solvedCourse1.setFirstTime(false);
        solvedCourseRepository.save(solvedCourse1);
    }

    public Boolean resetCourse(@NotNull String userId, @NotNull String courseId) {
        Boolean isDeleted = false;
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        SolvedCourse solvedCourse = solvedCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (solvedCourse == null)
            throw new IllegalArgumentException("Solution not found");

        for (Answer answer : solvedCourse.getFinalQuiz()) {
            answer.setUserAnswer("");
            answer.setIsCorrect(false);
        }

        solvedCourse.setGrade(0);
        solvedCourse.setFirstTime(true);

        solvedCourseRepository.save(solvedCourse);
        isDeleted = true;

        return isDeleted;
    }

    public Boolean attendLesson(@NotNull String userId, @NotNull String lessonId, @NotNull String level) {

        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        if (lessonRepository.findById(lessonId).isEmpty())
            throw new IllegalArgumentException("Lesson not found");

        if (!(level.equals("easy") || level.equals("medium") || level.equals("hard")))
            throw new IllegalArgumentException("Invalid level value should be easy or medium or hard");

        SolvedLesson solvedLesson = solvedLessonRepository.findByUserIdAndLessonIdAndLevel(userId, lessonId, level);
        if (solvedLesson == null) {
            SolvedLesson new_SolvedLesson = new SolvedLesson();
            new_SolvedLesson.setUserId(userId);
            new_SolvedLesson.setLessonId(lessonId);
            new_SolvedLesson.setLevel(level);
            ArrayList<Question> questions = lessonService.getAllQuestions(lessonId, level);

            for (Question question : questions) {
                Answer answer = new Answer();
                answer.setQuestionId(question.getId());
                answer.setQuestion(question.getQuestion());
                answer.setCorrectAnswer(question.getCorrectAnswer());
                answer.setUserAnswer("");
                answer.setExplaination(question.getExplanation());
                answer.setImage(question.getImage());
                answer.setCalc(question.getCalc());
                answer.setLevel(question.getLevel());
                answer.setFree(question.getFree());
                answer.setIsCorrect(false);
                answer.setOptions(question.getOptions());
                new_SolvedLesson.getLessonQuestions().add(answer);
            }
            solvedLessonRepository.save(new_SolvedLesson);
            return false;
        }

        // we need to update the solved lesson if a new question is added to the lesson
        // or a question is deleted

        ArrayList<Question> questions = lessonService.getAllQuestions(lessonId, level);
        for (Question question : questions) {
            Optional<Answer> optionalAnswer = solvedLesson.getLessonQuestions().stream()
                    .filter(a -> a.getQuestionId().equals(question.getId())).findFirst();
            if (optionalAnswer.isEmpty()) {
                Answer answer = new Answer();
                answer.setQuestionId(question.getId());
                answer.setQuestion(question.getQuestion());
                answer.setCorrectAnswer(question.getCorrectAnswer());
                answer.setUserAnswer("");
                answer.setExplaination(question.getExplanation());
                answer.setImage(question.getImage());
                answer.setIsCorrect(false);
                answer.setCalc(question.getCalc());
                answer.setLevel(question.getLevel());
                answer.setFree(question.getFree());
                answer.setOptions(question.getOptions());
                solvedLesson.getLessonQuestions().add(answer);
            }
        }

        // now we want to delete the quetions that are not in the lesson
        solvedLesson.getLessonQuestions().removeIf(answer -> questions.stream()
                .noneMatch(question -> question.getId().equals(answer.getQuestionId())));
        solvedLessonRepository.save(solvedLesson);

        Boolean firstTime = solvedLesson.getFirstTime();
        return !firstTime;
    }

    public void completeQuestion(@NotNull Answer answer, @NotNull String userId, @NotNull String lessonId,
            @NotNull String level) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        if (lessonRepository.findById(lessonId).isEmpty())
            throw new IllegalArgumentException("Lesson not found");

        if (!(level.equals("easy") || level.equals("medium") || level.equals("hard")))
            throw new IllegalArgumentException("Invalid level value should be easy or medium or hard");

        SolvedLesson solvedLesson = solvedLessonRepository.findByUserIdAndLessonIdAndLevel(userId, lessonId, level);

        Answer answer1 = solvedLesson.getLessonQuestions().stream()
                .filter(a -> a.getQuestion().equals(answer.getQuestion())).findFirst().get();
        answer1.setUserAnswer(answer.getUserAnswer());
        answer1.setIsCorrect(answer.getIsCorrect());

        int grade = 0;
        for (Answer answer2 : solvedLesson.getLessonQuestions()) {
            if (answer2.getIsCorrect())
                grade++;
        }

        solvedLesson.setGrade(grade);
        solvedLesson.setFirstTime(false);
        solvedLessonRepository.save(solvedLesson);
    }

    public Boolean resetLesson(@NotNull String userId, @NotNull String lessonId, @NotNull String level) {
        Boolean isDeleted = false;
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        SolvedLesson solvedLesson = solvedLessonRepository.findByUserIdAndLessonIdAndLevel(userId, lessonId, level);
        for (Answer answer : solvedLesson.getLessonQuestions()) {
            answer.setUserAnswer("");
            answer.setIsCorrect(false);
        }

        solvedLesson.setGrade(0);

        solvedLessonRepository.save(solvedLesson);

        isDeleted = true;

        return isDeleted;
    }

    public void enrollCourse(@NotNull String userId, @NotNull String courseId, @NotNull LocalDate expiryDate) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");
        if (courseRepository.findById(courseId).isEmpty())
            throw new IllegalArgumentException("Course not found");

        User user = userRepository.findById(userId).get();

        CourseDate courseDate = new CourseDate(courseId, expiryDate, courseService.getCourseName(courseId),
                courseService.getCourseImage(courseId));
        user.getCourses().add(courseDate);
        userRepository.save(user);
    }

    public String createReferralCode(@NotNull String userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        ReferralCode referralCode = new ReferralCode();
        referralCode.setUserId(userId);
        referralCode.setCode(UUID.randomUUID().toString().substring(0, 8));
        boolean isCodeExist = true;
        while (isCodeExist) {
            if (referralCodeRepository.findByCode(referralCode.getCode()) == null)
                isCodeExist = false;
            else
                referralCode.setCode(UUID.randomUUID().toString().substring(0, 8));
        }

        referralCodeRepository.save(referralCode);
        return referralCode.getCode();
    }

    public String createPromoCode(Integer discount, Integer expiryDateByMonth) {
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(UUID.randomUUID().toString().substring(0, 9));
        promoCode.setDiscount(discount);
        promoCode.setExpiryDateByMonth(expiryDateByMonth);
        promoCodeRepository.save(promoCode);

        return promoCode.getCode();
    }

    public void useReferralCode(@NotNull String code) {
        ReferralCode referralCode = referralCodeRepository.findByCode(code);
        if (referralCode == null)
            return;

        User user = userRepository.findById(referralCode.getUserId()).get();

        user.setWallet(user.getWallet() + Double.parseDouble(discountValue));
    }

    public User getProfile(@NotNull String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    public String forgotPassword(@NotNull String email) throws MessagingException, InterruptedException {

        // random otp from 5 digits
        int otp = (int) (Math.random() * (99999 - 10000 + 1) + 10000);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        user.setOtp(String.valueOf(otp));
        userRepository.save(user);

        String subject = "Reset Password";
        String body = "Your OTP is: " + otp;

        emailService.sendEmail(email, subject, body);

        return "OTP sent to your email";
    }

    public void resetPassword(@NotNull String userEmail, @NotNull String otp, @NotNull String newPassword) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setOtp(null);
        userRepository.save(user);
    }

    public Boolean resetQuestion(@NotNull String userId, @NotNull String lessonId, @NotNull String questionId,
            @NotNull String level) {
        Boolean isDeleted = false;
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        SolvedLesson solvedLesson = solvedLessonRepository.findByUserIdAndLessonIdAndLevel(userId, lessonId, level);
        Answer answer = solvedLesson.getLessonQuestions().stream()
                .filter(a -> a.getQuestionId().equals(questionId)).findFirst().get();

        if (answer == null)
            throw new IllegalArgumentException("Question not found");

        answer.setUserAnswer("");
        answer.setIsCorrect(false);

        solvedLessonRepository.save(solvedLesson);
        isDeleted = true;

        return isDeleted;
    }

    public SolvedLesson getLessonAnswers(@NotNull String userId, @NotNull String lessonId, @NotNull String level) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        if (lessonRepository.findById(lessonId).isEmpty())
            throw new IllegalArgumentException("Lesson not found");

        SolvedLesson solvedLesson = solvedLessonRepository.findByUserIdAndLessonIdAndLevel(userId, lessonId, level);
        if (solvedLesson == null)
            throw new IllegalArgumentException("Solution not found");

        return solvedLesson;
    }

    public SolvedCourse getCourseAnswers(@NotNull String userId, @NotNull String courseId) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        if (courseRepository.findById(courseId).isEmpty())
            throw new IllegalArgumentException("Course not found");

        SolvedCourse solvedCourse = solvedCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (solvedCourse == null)
            throw new IllegalArgumentException("Solution not found");

        return solvedCourse;
    }

    public ArrayList<CourseDate> getMyCourses(@NotNull String userId) {

        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        if (userRepository.findById(userId).isPresent()) {
            User user = userRepository.findById(userId).get();
            if (user.getCourses().isEmpty())
                return new ArrayList<CourseDate>();

            return user.getCourses();

        } else
            throw new IllegalArgumentException("User not found");

    }

    private CourseQuestions calculateCourseQuestions(List<Answer> answers) {
        int right = 0;
        int wrong = 0;
        int notSolved = 0;

        for (Answer answer : answers) {
            if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                right++;
            } else if (Boolean.FALSE.equals(answer.getIsCorrect()) && !answer.getUserAnswer().isEmpty()) {
                wrong++;
            } else {
                notSolved++;
            }
        }

        return new CourseQuestions(String.valueOf(right), String.valueOf(wrong), String.valueOf(notSolved));
    }

    private LessonQuestions calculateLessonQuestions(List<Answer> answers) {
        int right = 0;
        int wrong = 0;
        int notSolved = 0;

        for (Answer answer : answers) {
            if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                right++;
            } else if (Boolean.FALSE.equals(answer.getIsCorrect()) && !answer.getUserAnswer().isEmpty()) {
                wrong++;
            } else {
                notSolved++;
            }
        }

        return new LessonQuestions(String.valueOf(right), String.valueOf(wrong), String.valueOf(notSolved));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<String> deleteUser(@NotNull String userId) {
        if (userRepository.findById(userId).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        userRepository.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    public User getUserById(@NotNull String userId) {
        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        return userRepository.findById(userId).get();
    }

    public List<CourseInfo> getMyCoursesInfo(@NotNull String userId) {

        if (userRepository.findById(userId).isEmpty())
            throw new IllegalArgumentException("User not found");

        List<CourseInfo> courseInfos = new ArrayList<>();

        // Retrieve solved courses
        List<SolvedCourse> solvedCourses = solvedCourseRepository.findAllByUserId(userId);
        for (SolvedCourse course : solvedCourses) {
            CourseInfo courseInfo = new CourseInfo();
            courseInfo.setCourseId(course.getCourseId());
            courseInfo.setCourseName(courseService.getCourseName(course.getCourseId()));
            courseInfo.setCourseGrade(course.getGrade().toString());

            CourseQuestions courseQuestions = calculateCourseQuestions(course.getFinalQuiz());
            courseInfo.setCourseQuestions(courseQuestions);

            courseInfos.add(courseInfo);
        }

        // delete empty solved sourses from the list
        // if the not solved questions equal to the number of questions we will delete
        // it

        courseInfos.removeIf(courseInfo -> courseInfo.getCourseQuestions().getNotSolved()
                .equals(String.valueOf(courseService.getAllQuestions(courseInfo.getCourseId()).size())));

        return courseInfos;
    }

    public List<LessonInfo> getMyLessonsInfo(@NotNull String userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        List<LessonInfo> lessonInfos = new ArrayList<>();

        // Retrieve solved lessons
        List<SolvedLesson> solvedLessons = solvedLessonRepository.findAllByUserId(userId);
        for (SolvedLesson lesson : solvedLessons) {
            LessonInfo lessonInfo = new LessonInfo();
            if (lesson.getLessonId() == null) {
                continue;
            }
            if (lessonRepository.findById(lesson.getLessonId()).isEmpty()) {
                continue;
            }
            Lesson savedLesson = lessonRepository.findById(lesson.getLessonId()).get();
            lessonInfo.setCourseId(savedLesson.getCourseId());
            lessonInfo.setCourseName(courseService.getCourseName(savedLesson.getCourseId()));
            lessonInfo.setLessonId(lesson.getLessonId());
            lessonInfo.setLessonName(lessonService.getLessonName(lesson.getLessonId()));
            lessonInfo.setLessonGrade(lesson.getGrade().toString());
            lessonInfo.setLevel(lesson.getLevel());
            lessonInfo.setChapterId(savedLesson.getChapterId());
            if (chapterRepository.findById(savedLesson.getChapterId()).isPresent()) {
                lessonInfo.setChapterName(chapterRepository.findById(savedLesson.getChapterId()).get().getName());
            }
            LessonQuestions lessonQuestions = calculateLessonQuestions(lesson.getLessonQuestions());
            lessonInfo.setLessonQuestions(lessonQuestions);

            lessonInfos.add(lessonInfo);
        }

        return lessonInfos;
    }

    public void createDiscountValue(@NotNull String value) {
        if (discountRepository.findAll().isEmpty()) {
            DiscountValue discountValue = new DiscountValue();
            discountValue.setValue(value);
            discountRepository.save(discountValue);
        } else {
            DiscountValue discountValue = discountRepository.findAll().get(0);
            discountValue.setValue(value);
            discountRepository.save(discountValue);
        }
    }

}
