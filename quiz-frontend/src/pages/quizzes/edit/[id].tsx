import { useState, useEffect } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import QuizForm from '../../../components/Quiz/QuizForm';
import { getQuizById, updateQuiz } from '../../../services/quiz';
import { QuizFormData } from '../../../types/quiz';
import ProtectedRoute from '../../../components/ProtectedRoute';
import { useQuery } from '@tanstack/react-query';

const EditQuizPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Fetch quiz data
  const { data: quiz, isLoading, error } = useQuery({
    queryKey: ['quiz', id],
    queryFn: () => getQuizById(Number(id)),
    enabled: !!id, // Only run query when id is available
  });

  const handleSubmit = async (data: QuizFormData) => {
    if (!id) return;
    
    try {
      setIsSubmitting(true);
      await updateQuiz(Number(id), data);
      router.push('/quizzes');
    } catch (error) {
      console.error('Failed to update quiz:', error);
      alert('Failed to update quiz. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-500">Loading quiz data...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-lg font-medium text-red-600">Error loading quiz</h2>
          <p className="mt-2 text-gray-500">Failed to load quiz data. Please try again.</p>
          <Link
            href="/quizzes"
            className="mt-4 inline-flex items-center text-sm font-medium text-indigo-600 hover:text-indigo-500"
          >
            Return to Quizzes
          </Link>
        </div>
      </div>
    );
  }

  return (
    <ProtectedRoute requiredRole="TEACHER">
      <div className="min-h-screen bg-gray-100">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
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
                  aria-hidden="true"
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
            <h1 className="mt-4 text-2xl font-bold text-gray-900">Edit Quiz</h1>
            <p className="mt-1 text-sm text-gray-500">
              Update the quiz details below.
            </p>
          </div>

          <div className="bg-white shadow rounded-lg p-6">
            {quiz && <QuizForm initialData={quiz} onSubmit={handleSubmit} isSubmitting={isSubmitting} />}
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
};

export default EditQuizPage;