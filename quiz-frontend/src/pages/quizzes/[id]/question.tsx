import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import Link from 'next/link';
import { getQuizById } from '../../../services/quiz';
import { QuizDTO } from '../../../types/quiz';
import ProtectedRoute from '../../../components/ProtectedRoute';
import QuestionBank from '../../../components/Question/QuestionBank';

const QuizQuestionsPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const [quiz, setQuiz] = useState<QuizDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      const fetchQuiz = async () => {
        try {
          const data = await getQuizById(Number(id));
          setQuiz(data);
        } catch (err) {
          console.error('Failed to fetch quiz:', err);
        } finally {
          setLoading(false);
        }
      };
      fetchQuiz();
    }
  }, [id]);

  if (loading) return <div>Loading...</div>;
  if (!quiz) return <div>Quiz not found</div>;

  return (
    <ProtectedRoute requiredRole="TEACHER">
      <div className="min-h-screen bg-gray-100">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="mb-6">
            <div className="flex items-center">
              <Link
                href="/quizzes"
                className="inline-flex items-center text-sm font-medium text-indigo-600 hover:text-indigo-500"
              >
                <svg
                  className="mr-2 h-5 w-5"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fillRule="evenodd"
                    d="M7.707 14.707a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l2.293 2.293a1 1 0 010 1.414z"
                    clipRule="evenodd"
                  />
                </svg>
                Back to Quizzes
              </Link>
            </div>
            <h1 className="mt-4 text-2xl font-bold text-gray-900">
              Manage Questions: {quiz.title}
            </h1>
            <p className="mt-1 text-sm text-gray-500">
              Quiz Code: {quiz.code} | Total Questions: {quiz.numberOfQuestions}
            </p>
          </div>

          <div className="bg-white shadow rounded-lg p-6">
            <QuestionBank quizId={quiz.id!} />
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
};

export default QuizQuestionsPage;