package com.parth.quizapp.service;

import com.parth.quizapp.Model.*;
import com.parth.quizapp.dto.*;
import com.parth.quizapp.exceptions.ResourceNotFoundException;
import com.parth.quizapp.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepo quizAttemptRepository;

    @Autowired
    private StudentAnswerRepo studentAnswerRepository;

    @Autowired
    private quizRepo quizRepository;

    @Autowired
    private quesRepo questionRepository;

    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    // Start a quiz attempt
    @Transactional
    public QuizAttemptDTO startQuizAttempt(Long quizId) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if quiz is active
        if (!quiz.isActive()) {
            throw new RuntimeException("This quiz is not active");
        }

        // Check if student has already completed this quiz
        if (quizAttemptRepository.existsByStudentAndQuizAndCompleted(currentUser, quiz, true)) {
            throw new RuntimeException("You have already completed this quiz");
        }

        // Check if there's an incomplete attempt
        var existingAttempt = quizAttemptRepository.findByStudentAndQuizAndCompleted(
                currentUser, quiz, false);

        if (existingAttempt.isPresent()) {
            return convertToDTO(existingAttempt.get());
        }

        // Create new attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(currentUser);
        attempt.setQuiz(quiz);
        attempt.setTotalMarks(quiz.getMaxMarks());
        attempt.setTotalQuestions(quiz.getNumberOfQuestions());
        attempt.setStartTime(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToDTO(savedAttempt);
    }

    // Submit quiz answers and calculate score
    @Transactional
    public QuizResultDTO submitQuiz(QuizSubmissionDTO submissionDTO) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(submissionDTO.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        // Find the incomplete attempt
        QuizAttempt attempt = quizAttemptRepository.findByStudentAndQuizAndCompleted(
                        currentUser, quiz, false)
                .orElseThrow(() -> new RuntimeException("No active quiz attempt found"));

        // Get all questions for this quiz
        List<Ques> questions = questionRepository.findByQuiz(quiz);

        int correctAnswers = 0;
        int totalScore = 0;
        int marksPerQuestion = quiz.getMaxMarks() / quiz.getNumberOfQuestions();

        // Process each answer
        for (Ques question : questions) {
            String selectedAnswer = submissionDTO.getAnswers().get(question.getId());

            StudentAnswer studentAnswer = new StudentAnswer();
            studentAnswer.setQuizAttempt(attempt);
            studentAnswer.setQuestion(question);
            studentAnswer.setSelectedAnswer(selectedAnswer);
            studentAnswer.checkAnswer();

            if (studentAnswer.isCorrect()) {
                correctAnswers++;
                totalScore += marksPerQuestion;
            }

            studentAnswerRepository.save(studentAnswer);
        }

        // Update attempt with results
        attempt.setScore(totalScore);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.completeAttempt();

        QuizAttempt completedAttempt = quizAttemptRepository.save(attempt);

        return convertToResultDTO(completedAttempt);
    }

    // Get quiz results for a specific attempt
    public QuizResultDTO getQuizResult(Long attemptId) {
        User currentUser = getCurrentUser();

        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found"));

        // Check authorization
        boolean isStudent = attempt.getStudent().getId().equals(currentUser.getId());
        boolean isTeacher = (attempt.getQuiz().getCreator() != null &&
                attempt.getQuiz().getCreator().getId().equals(currentUser.getId())) ||
                (attempt.getQuiz().getTeacher() != null &&
                        attempt.getQuiz().getTeacher().getId().equals(currentUser.getId()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isStudent && !isTeacher && !isAdmin) {
            throw new RuntimeException("You are not authorized to view this result");
        }

        return convertToResultDTO(attempt);
    }

    // Get all quiz attempts for current student
    public List<QuizAttemptDTO> getMyQuizAttempts() {
        User currentUser = getCurrentUser();

        return quizAttemptRepository.findByStudentAndCompleted(currentUser, true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get all quiz results for quizzes created by current teacher
    public List<QuizAttemptDTO> getQuizResultsForMyQuizzes() {
        User currentUser = getCurrentUser();

        return quizAttemptRepository.findByTeacher(currentUser)
                .stream()
                .filter(QuizAttempt::isCompleted)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get all attempts for a specific quiz (for teachers)
    public List<QuizAttemptDTO> getQuizAttempts(Long quizId) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        // Check if user is authorized to view results
        boolean isTeacher = (quiz.getCreator() != null &&
                quiz.getCreator().getId().equals(currentUser.getId())) ||
                (quiz.getTeacher() != null &&
                        quiz.getTeacher().getId().equals(currentUser.getId()));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isTeacher && !isAdmin) {
            throw new RuntimeException("You are not authorized to view results for this quiz");
        }

        return quizAttemptRepository.findByQuiz(quiz)
                .stream()
                .filter(QuizAttempt::isCompleted)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convert QuizAttempt to DTO
    private QuizAttemptDTO convertToDTO(QuizAttempt attempt) {
        QuizAttemptDTO dto = new QuizAttemptDTO();
        dto.setId(attempt.getId());
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setQuizTitle(attempt.getQuiz().getTitle());

        // Handle user name - use username if firstName/lastName don't exist
        User student = attempt.getStudent();
        String studentName = getDisplayName(student);
        dto.setStudentName(studentName);

        // Handle email - use username if email doesn't exist
        String studentEmail = getEmail(student);
        dto.setStudentEmail(studentEmail);

        dto.setScore(attempt.getScore());
        dto.setTotalMarks(attempt.getTotalMarks());
        dto.setCorrectAnswers(attempt.getCorrectAnswers());
        dto.setTotalQuestions(attempt.getTotalQuestions());
        dto.setStartTime(attempt.getStartTime());
        dto.setEndTime(attempt.getEndTime());
        dto.setCompleted(attempt.isCompleted());

        if (attempt.getTotalMarks() > 0) {
            dto.setPercentage((double) attempt.getScore() / attempt.getTotalMarks() * 100);
        }

        return dto;
    }

    // Convert QuizAttempt to detailed result DTO
    private QuizResultDTO convertToResultDTO(QuizAttempt attempt) {
        QuizResultDTO resultDTO = new QuizResultDTO();
        resultDTO.setAttemptId(attempt.getId());
        resultDTO.setQuizTitle(attempt.getQuiz().getTitle());

        // Handle user name
        String studentName = getDisplayName(attempt.getStudent());
        resultDTO.setStudentName(studentName);

        resultDTO.setScore(attempt.getScore());
        resultDTO.setTotalMarks(attempt.getTotalMarks());
        resultDTO.setCorrectAnswers(attempt.getCorrectAnswers());
        resultDTO.setTotalQuestions(attempt.getTotalQuestions());
        resultDTO.setCompletedAt(attempt.getEndTime());

        if (attempt.getTotalMarks() > 0) {
            resultDTO.setPercentage((double) attempt.getScore() / attempt.getTotalMarks() * 100);
        }

        // Get detailed question results
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByQuizAttempt(attempt);
        List<QuestionResultDTO> questionResults = studentAnswers.stream()
                .map(this::convertToQuestionResultDTO)
                .collect(Collectors.toList());

        resultDTO.setQuestionResults(questionResults);

        return resultDTO;
    }

    // Helper method to get display name from User
    private String getDisplayName(User user) {
        try {
            // Try to use reflection to get firstName and lastName
            java.lang.reflect.Method getFirstName = user.getClass().getMethod("getFirstName");
            java.lang.reflect.Method getLastName = user.getClass().getMethod("getLastName");

            String firstName = (String) getFirstName.invoke(user);
            String lastName = (String) getLastName.invoke(user);

            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (lastName != null) {
                return lastName;
            }
        } catch (Exception e) {
            // If firstName/lastName don't exist, fall back to username
        }

        // Fallback to username
        return user.getUsername();
    }

    // Helper method to get email from User
    private String getEmail(User user) {
        try {
            // Try to use reflection to get email
            java.lang.reflect.Method getEmail = user.getClass().getMethod("getEmail");
            String email = (String) getEmail.invoke(user);

            if (email != null && !email.isEmpty()) {
                return email;
            }
        } catch (Exception e) {
            // If email doesn't exist, fall back to username
        }

        // Fallback to username
        return user.getUsername();
    }

    // Convert StudentAnswer to QuestionResultDTO
    private QuestionResultDTO convertToQuestionResultDTO(StudentAnswer studentAnswer) {
        QuestionResultDTO dto = new QuestionResultDTO();
        Ques question = studentAnswer.getQuestion();

        dto.setQuestionId(question.getId());
        dto.setQuestionContent(question.getContent());
        dto.setCorrectAnswer(question.getAnswer());
        dto.setSelectedAnswer(studentAnswer.getSelectedAnswer());
        dto.setCorrect(studentAnswer.isCorrect());
        dto.setOption1(question.getOption1());
        dto.setOption2(question.getOption2());
        dto.setOption3(question.getOption3());
        dto.setOption4(question.getOption4());

        return dto;
    }
}