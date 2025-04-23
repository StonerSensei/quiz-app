package com.parth.quizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Long id;
    private String code;
    private String title;
    private String description;
    private int maxMarks;
    private int numberOfQuestions;
    private boolean active;
}