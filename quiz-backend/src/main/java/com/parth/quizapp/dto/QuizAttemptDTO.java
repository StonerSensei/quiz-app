package com.parth.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDTO {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private String studentName;
    private String studentEmail;
    private int score;
    private int totalMarks;
    private int correctAnswers;
    private int totalQuestions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;
    private double percentage;
}