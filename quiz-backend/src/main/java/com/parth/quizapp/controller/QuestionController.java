package com.parth.quizapp.controller;

import com.parth.quizapp.dto.QuestionDTO;
import com.parth.quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    // Get questions for a quiz (without answers - for students)
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByQuizId(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.getQuestionsByQuizId(quizId));
    }
    // Get questions with answers (for teachers/admin)
    @GetMapping("/quiz/{quizId}/with-answers")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<QuestionDTO>> getQuestionsWithAnswersByQuizId(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.getQuestionsWithAnswersByQuizId(quizId));
    }
    // Get question by ID (without answer)
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.getQuestionById(questionId));
    }

    // Get question with answer (for teachers/admin)
    @GetMapping("/{questionId}/with-answer")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<QuestionDTO> getQuestionWithAnswerById(@PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.getQuestionWithAnswerById(questionId));
    }
    // Add question to quiz (teachers/admin)
    @PostMapping("/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<QuestionDTO> addQuestion(
            @PathVariable Long quizId,
            @RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionService.addQuestion(quizId, questionDTO));
    }
    // Update question (teachers/admin)
    @PutMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, questionDTO));
    }
    // Delete question (teachers/admin)
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok().build();
    }
    // Bulk add questions to quiz
    @PostMapping("/quiz/{quizId}/bulk")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<QuestionDTO>> addQuestionsBulk(
            @PathVariable Long quizId,
            @RequestBody List<QuestionDTO> questionDTOs) {
        List<QuestionDTO> savedQuestions = questionDTOs.stream()
                .map(questionDTO -> questionService.addQuestion(quizId, questionDTO))
                .toList();
        return ResponseEntity.ok(savedQuestions);
    }
}