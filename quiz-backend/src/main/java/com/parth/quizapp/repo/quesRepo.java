package com.parth.quizapp.repo;

import com.parth.quizapp.Model.Ques;
import com.parth.quizapp.Model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface quesRepo extends JpaRepository<Ques, Long> {
    List<Ques> findByQuiz(Quiz quiz);
    long countByQuiz(Quiz quiz);
}