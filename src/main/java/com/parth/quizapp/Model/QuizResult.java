package com.parth.quizapp.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int marksObtained;

    private int correctAnswers;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer totalQuestion;

    @Column(nullable = false)
    private Long studentId;

    private Long teacherId;

    private String studentName;

    private int attempted;

    private LocalDateTime attemptDate;

    @PrePersist
    public void prePersist() {
        this.attemptDate = LocalDateTime.now();
        if (this.user != null) {
            this.studentName = this.user.getUsername();
        }
    }
}