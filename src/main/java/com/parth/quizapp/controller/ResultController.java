package com.parth.quizapp.controller;

import com.parth.quizapp.dto.QuizResultDTO;
import com.parth.quizapp.dto.SubmitAnswerDTO;
import com.parth.quizapp.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    // Submit quiz answers and get result
    @PostMapping("/quiz/{quizId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<QuizResultDTO> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody List<SubmitAnswerDTO> answers) {
        return ResponseEntity.ok(resultService.submitQuiz(quizId, answers));
    }

    // Get current user's results
    @GetMapping("/my-results")
    public ResponseEntity<List<QuizResultDTO>> getMyResults() {
        return ResponseEntity.ok(resultService.getMyResults());
    }

    // Get result by ID (owner, quiz creator, or admin)
    @GetMapping("/{resultId}")
    public ResponseEntity<QuizResultDTO> getResultById(@PathVariable Long resultId) {
        return ResponseEntity.ok(resultService.getResultById(resultId));
    }

    // Get all results for a quiz (admin or quiz creator only)
    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<QuizResultDTO>> getResultsByQuizId(@PathVariable Long quizId) {
        return ResponseEntity.ok(resultService.getResultsByQuizId(quizId));
    }

    // Get student performance statistics
    @GetMapping("/stats/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> getStudentStats() {
        return ResponseEntity.ok(resultService.getStudentStatistics());
    }

    // Get quiz statistics (for teacher/admin)
    @GetMapping("/stats/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getQuizStats(@PathVariable Long quizId) {
        return ResponseEntity.ok(resultService.getQuizStatistics(quizId));
    }
}