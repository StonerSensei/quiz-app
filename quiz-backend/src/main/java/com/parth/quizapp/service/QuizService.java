package com.parth.quizapp.service;

import com.parth.quizapp.Model.Quiz;
import com.parth.quizapp.Model.User;
import com.parth.quizapp.dto.QuizDTO;
import com.parth.quizapp.exceptions.ResourceNotFoundException;
import com.parth.quizapp.repo.quesRepo;
import com.parth.quizapp.repo.quizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private quizRepo quizRepository;
    @Autowired
    private quesRepo quesRepository;

    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null ||
                !(authentication.getPrincipal() instanceof User)) {
            throw new RuntimeException("User not authenticated or invalid authentication");
        }
        return (User) authentication.getPrincipal();
    }

    // Convert Quiz to QuizDTO
    private QuizDTO convertToDTO(Quiz quiz) {
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(quiz.getId());
        quizDTO.setCode(quiz.getCode());
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

        if (quizDTO.getNumberOfQuestions() <= 0) {
            throw new IllegalArgumentException("Number of questions must be positive");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        // Generate a code if one is not provided
        if (quizDTO.getCode() == null || quizDTO.getCode().trim().isEmpty()) {
            quiz.setCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            quiz.setCode(quizDTO.getCode());
        }
        quiz.setDescription(quizDTO.getDescription());
        quiz.setMaxMarks(quizDTO.getMaxMarks());
        quiz.setNumberOfQuestions(quizDTO.getNumberOfQuestions());
        quiz.setActive(quizDTO.isActive());
        quiz.setCreator(currentUser);
        quiz.setTeacher(currentUser);

        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToDTO(savedQuiz);
    }

    // Update quiz
    public QuizDTO updateQuiz(Long quizId, QuizDTO quizDTO) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator of the quiz
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = quiz.getCreator().equals(currentUser) || quiz.getTeacher().equals(currentUser);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not authorized to update this quiz");
        }

        long existingQuestions = quesRepository.countByQuiz(quiz);
        if (quizDTO.getNumberOfQuestions() < existingQuestions) {
            throw new IllegalArgumentException(
                    String.format("Cannot reduce to %d questions. Already has %d questions",
                            quizDTO.getNumberOfQuestions(), existingQuestions)
            );
        }

        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setMaxMarks(quizDTO.getMaxMarks());
        quiz.setNumberOfQuestions(quizDTO.getNumberOfQuestions());
        quiz.setActive(quizDTO.isActive());

        // Update code if provided
        if (quizDTO.getCode() != null && !quizDTO.getCode().trim().isEmpty()) {
            quiz.setCode(quizDTO.getCode());
        }

        Quiz updatedQuiz = quizRepository.save(quiz);
        return convertToDTO(updatedQuiz);
    }

    // Delete quiz
    public void deleteQuiz(Long quizId) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator of the quiz
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = quiz.getCreator().equals(currentUser) || quiz.getTeacher().equals(currentUser);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not authorized to update this quiz");
        }

        quizRepository.delete(quiz);
    }

    // Toggle quiz active status (for Admin/Teacher)
    public QuizDTO toggleQuizActiveStatus(Long quizId) {
        User currentUser = getCurrentUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator/teacher of the quiz
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner =
                (quiz.getCreator() != null && quiz.getCreator().getId().equals(currentUser.getId())) ||
                        (quiz.getTeacher() != null && quiz.getTeacher().getId().equals(currentUser.getId()));

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not authorized to update this quiz");
        }

        quiz.setActive(!quiz.isActive());
        Quiz updatedQuiz = quizRepository.save(quiz);
        return convertToDTO(updatedQuiz);
    }

}