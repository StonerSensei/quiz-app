import api from './api';
import { QuestionDTO } from '../types/question';

export const getQuestionsByQuizId = async (quizId: number): Promise<QuestionDTO[]> => {
  const response = await api.get<QuestionDTO[]>(`/questions/quiz/${quizId}`);
  return response.data;
};

export const getQuestionsWithAnswersByQuizId = async (quizId: number): Promise<QuestionDTO[]> => {
  const response = await api.get<QuestionDTO[]>(`/questions/quiz/${quizId}/with-answers`);
  return response.data;
};

export const addQuestion = async (quizId: number, questionData: QuestionDTO): Promise<QuestionDTO> => {
  const response = await api.post<QuestionDTO>(`/questions/quiz/${quizId}`, questionData);
  return response.data;
};

export const updateQuestion = async (questionId: number, questionData: QuestionDTO): Promise<QuestionDTO> => {
  const response = await api.put<QuestionDTO>(`/questions/${questionId}`, questionData);
  return response.data;
};

export const deleteQuestion = async (questionId: number): Promise<void> => {
  await api.delete(`/questions/${questionId}`);
};

export const addQuestionsBulk = async (quizId: number, questions: QuestionDTO[]): Promise<QuestionDTO[]> => {
  const response = await api.post<QuestionDTO[]>(`/questions/quiz/${quizId}/bulk`, questions);
  return response.data;
};