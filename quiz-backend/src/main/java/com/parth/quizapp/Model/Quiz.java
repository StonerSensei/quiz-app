package com.parth.quizapp.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    private String title;
    private String description;
    private int maxMarks;
    private int numberOfQuestions;
    private boolean active = true;
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ques> questions = new ArrayList<>();
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.code == null || this.code.isEmpty()) {
            this.code = UUID.randomUUID().toString().substring(0, 8);
        }
    }
}