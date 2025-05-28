export interface QuestionDTO {
  id?: number;
  content: string;
  image?: string;
  option1: string;
  option2: string;
  option3: string;
  option4: string;
  answer: string;
  quizId?: number;
}

export interface QuestionFormData {
  content: string;
  image?: string;
  option1: string;
  option2: string;
  option3: string;
  option4: string;
  answer: string;
}