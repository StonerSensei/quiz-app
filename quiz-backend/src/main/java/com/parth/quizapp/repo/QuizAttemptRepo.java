package com.parth.quizapp.repo;

import com.parth.quizapp.Model.Quiz;
import com.parth.quizapp.Model.QuizAttempt;
import com.parth.quizapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepo extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByStudent(User student);
    List<QuizAttempt> findByQuiz(Quiz quiz);
    List<QuizAttempt> findByStudentAndCompleted(User student, boolean completed);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz IN :quizzes")
    List<QuizAttempt> findByQuizIn(@Param("quizzes") List<Quiz> quizzes);

    Optional<QuizAttempt> findByStudentAndQuizAndCompleted(User student, Quiz quiz, boolean completed);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz.creator = :teacher OR qa.quiz.teacher = :teacher")
    List<QuizAttempt> findByTeacher(@Param("teacher") User teacher);

    boolean existsByStudentAndQuizAndCompleted(User student, Quiz quiz, boolean completed);
}