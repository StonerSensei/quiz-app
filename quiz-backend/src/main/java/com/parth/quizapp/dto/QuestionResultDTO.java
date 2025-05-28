package com.parth.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    private Long questionId;
    private String questionContent;
    private String correctAnswer;
    private String selectedAnswer;
    private boolean isCorrect;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}