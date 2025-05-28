package com.parth.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private Long attemptId;
    private String quizTitle;
    private String studentName;
    private int score;
    private int totalMarks;
    private int correctAnswers;
    private int totalQuestions;
    private double percentage;
    private LocalDateTime completedAt;
    private List<QuestionResultDTO> questionResults;
}