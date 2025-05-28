package com.parth.quizapp.service;

import com.parth.quizapp.Model.Ques;
import com.parth.quizapp.Model.Quiz;
import com.parth.quizapp.Model.User;
import com.parth.quizapp.dto.QuestionDTO;
import com.parth.quizapp.exceptions.ResourceNotFoundException;
import com.parth.quizapp.repo.quesRepo;
import com.parth.quizapp.repo.quizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private quesRepo questionRepository;

    @Autowired
    private quizRepo quizRepository;

    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    // DTO conversion methods
    private QuestionDTO convertToDTO(Ques question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setImage(question.getImage());
        dto.setOption1(question.getOption1());
        dto.setOption2(question.getOption2());
        dto.setOption3(question.getOption3());
        dto.setOption4(question.getOption4());
        dto.setQuizId(question.getQuiz().getId());
        return dto;
    }

    private QuestionDTO convertToDTOWithAnswer(Ques question) {
        QuestionDTO dto = convertToDTO(question);
        dto.setAnswer(question.getAnswer());
        return dto;
    }

    // Get questions by quiz ID (without answers - for students taking quiz)
    public List<QuestionDTO> getQuestionsByQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if quiz is active or user is admin/creator
        User currentUser = getCurrentUser();
        boolean isAdminOrCreator = currentUser.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"))
                && (quiz.getCreator().equals(currentUser) ||
                currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        if (!quiz.isActive() && !isAdminOrCreator) {
            throw new RuntimeException("This quiz is not active");
        }

        return questionRepository.findByQuiz(quiz).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get questions with answers (for admin/teacher)
    public List<QuestionDTO> getQuestionsWithAnswersByQuizId(Long quizId) {
        User currentUser = getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !quiz.getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to view answers for this quiz");
        }

        return questionRepository.findByQuiz(quiz).stream()
                .map(this::convertToDTOWithAnswer)
                .collect(Collectors.toList());
    }

    // Get a single question (without answer)
    public QuestionDTO getQuestionById(Long questionId) {
        Ques question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        return convertToDTO(question);
    }

    // Get a single question with answer (for admin/teacher)
    public QuestionDTO getQuestionWithAnswerById(Long questionId) {
        User currentUser = getCurrentUser();
        Ques question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !question.getQuiz().getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to view the answer for this question");
        }

        return convertToDTOWithAnswer(question);
    }

    // Add question to quiz
    public QuestionDTO addQuestion(Long quizId, QuestionDTO questionDTO) {
        User currentUser = getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !quiz.getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to add questions to this quiz");
        }

        Ques question = new Ques();
        question.setContent(questionDTO.getContent());
        question.setImage(questionDTO.getImage());
        question.setOption1(questionDTO.getOption1());
        question.setOption2(questionDTO.getOption2());
        question.setOption3(questionDTO.getOption3());
        question.setOption4(questionDTO.getOption4());
        question.setAnswer(questionDTO.getAnswer());
        question.setQuiz(quiz);

        Ques savedQuestion = questionRepository.save(question);

        // Update number of questions in quiz
//        quiz.setNumberOfQuestions(quiz.getNumberOfQuestions() + 1);
//        quizRepository.save(quiz);

        return convertToDTOWithAnswer(savedQuestion);
    }

    // Update question
    public QuestionDTO updateQuestion(Long questionId, QuestionDTO questionDTO) {
        User currentUser = getCurrentUser();
        Ques question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !question.getQuiz().getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to update this question");
        }

        question.setContent(questionDTO.getContent());
        question.setImage(questionDTO.getImage());
        question.setOption1(questionDTO.getOption1());
        question.setOption2(questionDTO.getOption2());
        question.setOption3(questionDTO.getOption3());
        question.setOption4(questionDTO.getOption4());
        question.setAnswer(questionDTO.getAnswer());

        Ques updatedQuestion = questionRepository.save(question);
        return convertToDTOWithAnswer(updatedQuestion);
    }

    // Delete question
    public void deleteQuestion(Long questionId) {
        User currentUser = getCurrentUser();
        Ques question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        // Check if user is admin or the creator of the quiz
        if (!currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !question.getQuiz().getCreator().equals(currentUser)) {
            throw new RuntimeException("You are not authorized to delete this question");
        }

        Quiz quiz = question.getQuiz();
        questionRepository.delete(question);

        // Update number of questions in quiz
//        quiz.setNumberOfQuestions(quiz.getNumberOfQuestions() - 1);
        quizRepository.save(quiz);
    }
}