export interface QuizDTO {
  id?: number;
  code?: string;
  title: string;
  description: string;
  maxMarks: number;
  numberOfQuestions: number;
  active?: boolean;
}

export interface QuizFormData {
  title: string;
  code?: string;
  description: string;
  maxMarks: number;
  numberOfQuestions: number;
  active: boolean;
}
