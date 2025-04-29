package com.parth.quizapp.service;

import com.parth.quizapp.Model.Ques;
import com.parth.quizapp.Model.Quiz;
import com.parth.quizapp.Model.QuizResult;
import com.parth.quizapp.Model.User;
import com.parth.quizapp.dto.QuizResultDTO;
import com.parth.quizapp.dto.SubmitAnswerDTO;
import com.parth.quizapp.exceptions.ResourceNotFoundException;
import com.parth.quizapp.repo.QuizResultRepo;
import com.parth.quizapp.repo.quesRepo;
import com.parth.quizapp.repo.quizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResultService {

    @Autowired
    private QuizResultRepo resultRepository;

    @Autowired
    private quizRepo quizRepository;

    @Autowired
    private quesRepo questionRepository;

    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    // DTO conversion
    private QuizResultDTO convertToDTO(QuizResult result) {
        QuizResultDTO dto = new QuizResultDTO();
        dto.setId(result.getId());
        dto.setQuizId(result.getQuiz().getId());
        dto.setQuizTitle(result.getQuiz().getTitle());
        dto.setUserId(result.getUser().getId());
        dto.setUsername(result.getUser().getUsername());
        dto.setMarksObtained(result.getMarksObtained());
        dto.setCorrectAnswers(result.getCorrectAnswers());
        dto.setAttempted(result.getAttempted());
        dto.setTotalQuestions(result.getTotalQuestion());
        dto.setMaxMarks(result.getQuiz().getMaxMarks());
        dto.setAttemptDate(result.getAttemptDate());
        dto.setStudentId(result.getStudentId());
        dto.setTeacherId(result.getTeacherId());
        dto.setStudentName(result.getUser().getUsername());
        dto.setTotalQuestion(result.getTotalQuestion());
        dto.setScore(result.getScore());
        return dto;
    }

    // Submit quiz answers and calculate result
    public QuizResultDTO submitQuiz(Long quizId, List<SubmitAnswerDTO> answers) {
        User currentUser = getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if quiz is active
        if (!quiz.isActive()) {
            throw new RuntimeException("This quiz is not active");
        }

        // Calculate result
        int correctAnswers = 0;
        int attempted = answers.size();

        // Create a map of question id to user answer for efficient lookup
        Map<Long, String> userAnswers = new HashMap<>();
        for (SubmitAnswerDTO answer : answers) {
            userAnswers.put(answer.getQuestionId(), answer.getSelectedAnswer());
        }

        // Get all questions for this quiz
        List<Ques> questions = questionRepository.findByQuiz(quiz);

        // Check answers
        for (Ques question : questions) {
            String userAnswer = userAnswers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getAnswer())) {
                correctAnswers++;
            }
        }

        // Calculate marks
        double marksPerQuestion = (double) quiz.getMaxMarks() / quiz.getNumberOfQuestions();
        int marksObtained = (int) Math.round(correctAnswers * marksPerQuestion);

        // Save result
        QuizResult result = new QuizResult();
        result.setQuiz(quiz);
        result.setUser(currentUser);
        result.setMarksObtained(marksObtained);
        result.setCorrectAnswers(correctAnswers);
        result.setAttempted(attempted);
        result.setScore(marksObtained);  // Using marksObtained as score
        result.setTotalQuestion(quiz.getNumberOfQuestions());
        result.setStudentId(currentUser.getId());  // Set student ID from current user

        // Check if quiz has creator and set teacher ID
        if (quiz.getCreator() != null) {
            result.setTeacherId(quiz.getCreator().getId());
        }

        QuizResult savedResult = resultRepository.save(result);
        return convertToDTO(savedResult);
    }

    // Get results for current user
    public List<QuizResultDTO> getMyResults() {
        User currentUser = getCurrentUser();
        return resultRepository.findByUser(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get result by ID (only for admin, teacher who created the quiz, or the student who took the quiz)
    public QuizResultDTO getResultById(Long resultId) {
        User currentUser = getCurrentUser();
        QuizResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with id: " + resultId));

        // Check authorization
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isQuizCreator = result.getQuiz().getCreator().equals(currentUser);
        boolean isStudent = result.getUser().equals(currentUser);

        if (!isAdmin && !isQuizCreator && !isStudent) {
            throw new RuntimeException("You are not authorized to view this result");
        }

        return convertToDTO(result);
    }

    // Get all results for a quiz (admin or quiz creator only)
    public List<QuizResultDTO> getResultsByQuizId(Long quizId) {
        User currentUser = getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check authorization
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isQuizCreator = quiz.getCreator().equals(currentUser);

        if (!isAdmin && !isQuizCreator) {
            throw new RuntimeException("You are not authorized to view results for this quiz");
        }

        return resultRepository.findByQuiz(quiz).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get student statistics
    public Map<String, Object> getStudentStatistics() {
        User currentUser = getCurrentUser();
        List<QuizResult> results = resultRepository.findByUser(currentUser);

        Map<String, Object> statistics = new HashMap<>();

        // Total quizzes attempted
        statistics.put("totalQuizzesAttempted", results.size());

        // Calculate average score
        double totalScore = 0;
        int totalMaxMarks = 0;

        for (QuizResult result : results) {
            totalScore += result.getMarksObtained();
            totalMaxMarks += result.getQuiz().getMaxMarks();
        }

        double averagePercentage = totalMaxMarks > 0 ? (totalScore / totalMaxMarks) * 100 : 0;
        statistics.put("averagePercentage", Math.round(averagePercentage * 100.0) / 100.0);

        // Best score
        if (!results.isEmpty()) {
            QuizResult bestResult = results.stream()
                    .max((r1, r2) -> {
                        double p1 = (double) r1.getMarksObtained() / r1.getQuiz().getMaxMarks();
                        double p2 = (double) r2.getMarksObtained() / r2.getQuiz().getMaxMarks();
                        return Double.compare(p1, p2);
                    })
                    .orElse(null);

            if (bestResult != null) {
                Map<String, Object> bestQuiz = new HashMap<>();
                bestQuiz.put("quizTitle", bestResult.getQuiz().getTitle());
                bestQuiz.put("score", bestResult.getMarksObtained());
                bestQuiz.put("maxMarks", bestResult.getQuiz().getMaxMarks());
                bestQuiz.put("percentage",
                        Math.round(((double) bestResult.getMarksObtained() / bestResult.getQuiz().getMaxMarks() * 100) * 100.0) / 100.0);

                statistics.put("bestPerformance", bestQuiz);
            }
        }

        return statistics;
    }

    // Get quiz statistics
    public Map<String, Object> getQuizStatistics(Long quizId) {
        User currentUser = getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check authorization
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isQuizCreator = quiz.getCreator().equals(currentUser);

        if (!isAdmin && !isQuizCreator) {
            throw new RuntimeException("You are not authorized to view statistics for this quiz");
        }

        List<QuizResult> results = resultRepository.findByQuiz(quiz);
        Map<String, Object> statistics = new HashMap<>();

        // Basic info
        statistics.put("quizTitle", quiz.getTitle());
        statistics.put("totalAttempts", results.size());

        // Calculate average score
        double totalScore = 0;
        for (QuizResult result : results) {
            totalScore += result.getMarksObtained();
        }

        double averageScore = results.isEmpty() ? 0 : totalScore / results.size();
        statistics.put("averageScore", Math.round(averageScore * 100.0) / 100.0);

        // Highest and lowest scores
        if (!results.isEmpty()) {
            QuizResult highestResult = results.stream()
                    .max((r1, r2) -> Integer.compare(r1.getMarksObtained(), r2.getMarksObtained()))
                    .orElse(null);

            QuizResult lowestResult = results.stream()
                    .min((r1, r2) -> Integer.compare(r1.getMarksObtained(), r2.getMarksObtained()))
                    .orElse(null);

            if (highestResult != null) {
                Map<String, Object> highest = new HashMap<>();
                highest.put("score", highestResult.getMarksObtained());
                highest.put("username", highestResult.getUser().getUsername());
                highest.put("percentage",
                        Math.round(((double) highestResult.getMarksObtained() / quiz.getMaxMarks() * 100) * 100.0) / 100.0);

                statistics.put("highestScore", highest);
            }

            if (lowestResult != null) {
                Map<String, Object> lowest = new HashMap<>();
                lowest.put("score", lowestResult.getMarksObtained());
                lowest.put("username", lowestResult.getUser().getUsername());
                lowest.put("percentage",
                        Math.round(((double) lowestResult.getMarksObtained() / quiz.getMaxMarks() * 100) * 100.0) / 100.0);

                statistics.put("lowestScore", lowest);
            }
        }

        // Score distribution
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("90-100", 0);
        distribution.put("80-89", 0);
        distribution.put("70-79", 0);
        distribution.put("60-69", 0);
        distribution.put("50-59", 0);
        distribution.put("0-49", 0);

        for (QuizResult result : results) {
            double percentage = (double) result.getMarksObtained() / quiz.getMaxMarks() * 100;

            if (percentage >= 90) {
                distribution.put("90-100", distribution.get("90-100") + 1);
            } else if (percentage >= 80) {
                distribution.put("80-89", distribution.get("80-89") + 1);
            } else if (percentage >= 70) {
                distribution.put("70-79", distribution.get("70-79") + 1);
            } else if (percentage >= 60) {
                distribution.put("60-69", distribution.get("60-69") + 1);
            } else if (percentage >= 50) {
                distribution.put("50-59", distribution.get("50-59") + 1);
            } else {
                distribution.put("0-49", distribution.get("0-49") + 1);
            }
        }

        statistics.put("scoreDistribution", distribution);

        return statistics;
    }
}