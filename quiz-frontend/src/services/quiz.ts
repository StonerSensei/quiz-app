import api from './api';
import { QuizDTO} from '../types/quiz';

// Get all quizzes (admin only)
export const getAllQuizzes = async (): Promise<QuizDTO[]> => {
  const response = await api.get<QuizDTO[]>('/quizzes/all');
  return response.data;
};

// Get active quizzes (all authenticated users)
export const getActiveQuizzes = async (): Promise<QuizDTO[]> => {
  const response = await api.get<QuizDTO[]>('/quizzes/active');
  return response.data;
};

// Get quizzes created by current user (teacher/admin)
export const getMyQuizzes = async (): Promise<QuizDTO[]> => {
  const response = await api.get<QuizDTO[]>('/quizzes/my-quizzes');
  return response.data;
};

// Get quiz by ID (includes questions)
export const getQuizById = async (quizId: number): Promise<QuizDTO> => {
  const response = await api.get<QuizDTO>(`/quizzes/${quizId}`);
  return response.data;
};

// Create new quiz
export const createQuiz = async (quizData: QuizDTO): Promise<QuizDTO> => {
  const response = await api.post<QuizDTO>('/quizzes', quizData);
  return response.data;
};

// Update quiz
export const updateQuiz = async (quizId: number, quizData: QuizDTO): Promise<QuizDTO> => {
  const response = await api.put<QuizDTO>(`/quizzes/${quizId}`, quizData);
  return response.data;
};

// Delete quiz
export const deleteQuiz = async (quizId: number): Promise<void> => {
  await api.delete(`/quizzes/${quizId}`);
};

// Toggle quiz active status
export const toggleQuizActiveStatus = async (quizId: number): Promise<QuizDTO> => {
  const response = await api.put<QuizDTO>(`/quizzes/${quizId}/toggle-active`);
  return response.data;
};
