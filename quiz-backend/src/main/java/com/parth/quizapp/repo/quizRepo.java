package com.parth.quizapp.repo;

import com.parth.quizapp.Model.Quiz;
import com.parth.quizapp.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface quizRepo extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCreator(User creator);
    List<Quiz> findByActive(boolean active);
}