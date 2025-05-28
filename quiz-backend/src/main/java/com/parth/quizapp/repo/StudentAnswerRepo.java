package com.parth.quizapp.repo;

import com.parth.quizapp.Model.QuizAttempt;
import com.parth.quizapp.Model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepo extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByQuizAttempt(QuizAttempt quizAttempt);
}