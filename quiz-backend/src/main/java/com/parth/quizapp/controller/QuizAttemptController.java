package com.parth.quizapp.controller;

import com.parth.quizapp.dto.*;
import com.parth.quizapp.service.QuizAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    // Start a quiz attempt (for students)
    @PostMapping("/start/{quizId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<QuizAttemptDTO> startQuizAttempt(@PathVariable Long quizId) {
        QuizAttemptDTO attempt = quizAttemptService.startQuizAttempt(quizId);
        return ResponseEntity.ok(attempt);
    }

    // Submit quiz answers and get results
    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<QuizResultDTO> submitQuiz(@RequestBody QuizSubmissionDTO submissionDTO) {
        QuizResultDTO result = quizAttemptService.submitQuiz(submissionDTO);
        return ResponseEntity.ok(result);
    }

    // Get specific quiz result by attempt ID
    @GetMapping("/result/{attemptId}")
    public ResponseEntity<QuizResultDTO> getQuizResult(@PathVariable Long attemptId) {
        QuizResultDTO result = quizAttemptService.getQuizResult(attemptId);
        return ResponseEntity.ok(result);
    }

    // Get all quiz attempts for current student
    @GetMapping("/my-attempts")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<List<QuizAttemptDTO>> getMyQuizAttempts() {
        List<QuizAttemptDTO> attempts = quizAttemptService.getMyQuizAttempts();
        return ResponseEntity.ok(attempts);
    }

    // Get all quiz results for quizzes created by current teacher
    @GetMapping("/my-quiz-results")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<QuizAttemptDTO>> getQuizResultsForMyQuizzes() {
        List<QuizAttemptDTO> results = quizAttemptService.getQuizResultsForMyQuizzes();
        return ResponseEntity.ok(results);
    }

    // Get all attempts for a specific quiz (for teachers to see who took their quiz)
    @GetMapping("/quiz/{quizId}/attempts")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<QuizAttemptDTO>> getQuizAttempts(@PathVariable Long quizId) {
        List<QuizAttemptDTO> attempts = quizAttemptService.getQuizAttempts(quizId);
        return ResponseEntity.ok(attempts);
    }
}