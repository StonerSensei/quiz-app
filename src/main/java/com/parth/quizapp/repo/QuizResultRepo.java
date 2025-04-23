package com.parth.quizapp.repo;

import com.parth.quizapp.Model.Quiz;
import com.parth.quizapp.Model.QuizResult;
import com.parth.quizapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepo extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUser(User user);
    List<QuizResult> findByQuiz(Quiz quiz);
    List<QuizResult> findByUserAndQuiz(User user, Quiz quiz);
}