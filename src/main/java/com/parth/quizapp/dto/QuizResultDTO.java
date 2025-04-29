package com.parth.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Long userId;
    private String username;
    private int marksObtained;
    private int correctAnswers;
    private int attempted;
    private int totalQuestions;  // For backward compatibility
    private int maxMarks;
    private LocalDateTime attemptDate;
    private Long studentId;
    private Long teacherId;
    private String studentName;
    private Integer totalQuestion;  // Match DB field name
    private Integer score;
}