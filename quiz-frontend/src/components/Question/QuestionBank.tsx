import { useState, useEffect } from 'react';
import { QuestionDTO } from '../../types/question';
import { 
  getQuestionsWithAnswersByQuizId, 
  deleteQuestion,
  addQuestion,
  updateQuestion
} from '../../services/question';
import QuestionForm from './QuestionForm';
import Modal from '../common/Modal';

interface QuestionBankProps {
  quizId: number;
}

const QuestionBank = ({ quizId }: QuestionBankProps) => {
  const [questions, setQuestions] = useState<QuestionDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentQuestion, setCurrentQuestion] = useState<QuestionDTO | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchQuestions = async () => {
    try {
      setLoading(true);
      const data = await getQuestionsWithAnswersByQuizId(quizId);
      setQuestions(data);
    } catch (err) {
      setError('Failed to fetch questions');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchQuestions();
  }, [quizId]);

  const handleAddQuestion = () => {
    setCurrentQuestion(null);
    setIsModalOpen(true);
  };

  const handleEditQuestion = (question: QuestionDTO) => {
    setCurrentQuestion(question);
    setIsModalOpen(true);
  };

  const handleDeleteQuestion = async (questionId: number) => {
    if (window.confirm('Are you sure you want to delete this question?')) {
      try {
        await deleteQuestion(questionId);
        fetchQuestions();
      } catch (err) {
        setError('Failed to delete question');
        console.error(err);
      }
    }
  };

  const handleSubmitQuestion = async (data: QuestionDTO) => {
    try {
      setIsSubmitting(true);
      if (currentQuestion) {
        await updateQuestion(currentQuestion.id!, data);
      } else {
        await addQuestion(quizId, data);
      }
      setIsModalOpen(false);
      fetchQuestions();
    } catch (err) {
      console.error('Failed to save question:', err);
      setError('Failed to save question');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) return <div>Loading questions...</div>;
  if (error) return <div className="text-red-600">{error}</div>;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-xl font-semibold">Question Bank</h2>
        <button
          onClick={handleAddQuestion}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          Add Question
        </button>
      </div>

      {questions.length === 0 ? (
        <div className="text-center py-8 bg-gray-50 rounded-lg">
          <p className="text-gray-500">No questions added yet</p>
        </div>
      ) : (
        <div className="space-y-4">
          {questions.map((question) => (
            <div key={question.id} className="bg-white shadow overflow-hidden rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <div className="flex justify-between">
                  <h3 className="text-lg font-medium text-gray-900">{question.content}</h3>
                  <div className="flex space-x-2">
                    <button
                      onClick={() => handleEditQuestion(question)}
                      className="text-indigo-600 hover:text-indigo-900"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeleteQuestion(question.id!)}
                      className="text-red-600 hover:text-red-900"
                    >
                      Delete
                    </button>
                  </div>
                </div>
                <div className="mt-2 grid grid-cols-1 md:grid-cols-2 gap-2">
                  <div className={`p-2 rounded ${question.answer === 'option1' ? 'bg-green-100' : 'bg-gray-100'}`}>
                    <span className="font-medium">A:</span> {question.option1}
                  </div>
                  <div className={`p-2 rounded ${question.answer === 'option2' ? 'bg-green-100' : 'bg-gray-100'}`}>
                    <span className="font-medium">B:</span> {question.option2}
                  </div>
                  <div className={`p-2 rounded ${question.answer === 'option3' ? 'bg-green-100' : 'bg-gray-100'}`}>
                    <span className="font-medium">C:</span> {question.option3}
                  </div>
                  <div className={`p-2 rounded ${question.answer === 'option4' ? 'bg-green-100' : 'bg-gray-100'}`}>
                    <span className="font-medium">D:</span> {question.option4}
                  </div>
                </div>
                {question.image && (
                  <div className="mt-4">
                    <img src={question.image} alt="Question illustration" className="max-w-full h-auto rounded" />
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={currentQuestion ? 'Edit Question' : 'Add Question'}>
        <QuestionForm
          initialData={currentQuestion || undefined}
          onSubmit={handleSubmitQuestion}
          isSubmitting={isSubmitting}
        />
      </Modal>
    </div>
  );
};

export default QuestionBank;