package com.parth.quizapp.service;

import com.parth.quizapp.Model.Quiz;
import com.parth.quizapp.Model.User;
import com.parth.quizapp.dto.QuizDTO;
import com.parth.quizapp.exceptions.ResourceNotFoundException;
import com.parth.quizapp.repo.quizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private quizRepo quizRepository;

    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    // Convert Quiz to QuizDTO
    private QuizDTO convertToDTO(Quiz quiz) {
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(quiz.getId());
        quizDTO.setTitle(quiz.getTitle());
        quizDTO.setDescription(quiz.getDescription());
        quizDTO.setMaxMarks(quiz.getMaxMarks());
        quizDTO.setNumberOfQuestions(quiz.getNumberOfQuestions());
        quizDTO.setActive(quiz.isActive());
        return quizDTO;
    }

    // Get all quizzes (Admin access)
    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get active quizzes (Student access)
    public List<QuizDTO> getActiveQuizzes() {
        return quizRepository.findByActive(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get quizzes created by current user (Teacher access)
    public List<QuizDTO> getMyQuizzes() {
        User currentUser = getCurrentUser();
        return quizRepository.findByCreator(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get quiz by ID
    public QuizDTO getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        return convertToDTO(quiz);
    }

    // Create new quiz
    public QuizDTO createQuiz(QuizDTO quizDTO) {
        User currentUser = getCurrentUser();

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setMaxMarks(quizDTO.getMaxMarks());
        quiz.setNumberOfQuestions(quizDTO.getNumberOfQuestions());
        quiz.setActive(quizDTO.isActive());
        quiz.setCreator(currentUser);

        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToDTO(savedQuiz);
    }

    // Update quiz
    public QuizDTO updateQuiz(Long quizId, QuizDTO quizDTO) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !quiz.getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to update this quiz");
        }

        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setMaxMarks(quizDTO.getMaxMarks());
        quiz.setNumberOfQuestions(quizDTO.getNumberOfQuestions());
        quiz.setActive(quizDTO.isActive());

        Quiz updatedQuiz = quizRepository.save(quiz);
        return convertToDTO(updatedQuiz);
    }

    // Delete quiz
    public void deleteQuiz(Long quizId) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !quiz.getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to delete this quiz");
        }

        quizRepository.delete(quiz);
    }

    // Toggle quiz active status (for Admin/Teacher)
    public QuizDTO toggleQuizActiveStatus(Long quizId) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !quiz.getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to update this quiz");
        }

        quiz.setActive(!quiz.isActive());
        Quiz updatedQuiz = quizRepository.save(quiz);
        return convertToDTO(updatedQuiz);
    }
}