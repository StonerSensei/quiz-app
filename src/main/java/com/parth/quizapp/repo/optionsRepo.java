package com.parth.quizapp.repo;

import com.parth.quizapp.Model.Options;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface optionsRepo extends JpaRepository<Options, Long> {
    List<Options> findByQuestionId(Long questionId);
}
