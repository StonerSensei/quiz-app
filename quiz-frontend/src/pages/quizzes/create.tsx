import { useState } from 'react';
import { useRouter } from 'next/router';
import Link from 'next/link';
import QuizForm from '../../components/Quiz/QuizForm';
import { createQuiz } from '../../services/quiz';
import { QuizFormData } from '../../types/quiz';
import ProtectedRoute from '../../components/ProtectedRoute';
import QuestionBank from '../../components/Question/QuestionBank';

const CreateQuizPage = () => {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [createdQuizId, setCreatedQuizId] = useState<number | null>(null);

  const handleSubmit = async (data: QuizFormData) => {
    try {
      setIsSubmitting(true);
      const createdQuiz = await createQuiz(data);
      setCreatedQuizId(createdQuiz.id!);
    } catch (error) {
      console.error('Failed to create quiz:', error);
      alert('Failed to create quiz. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

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
            <h1 className="mt-4 text-2xl font-bold text-gray-900">
              {createdQuizId ? 'Add Questions to Quiz' : 'Create New Quiz'}
            </h1>
            <p className="mt-1 text-sm text-gray-500">
              {createdQuizId
                ? 'Add questions to your newly created quiz below.'
                : 'Fill in the details below to create a new quiz. You can edit your quiz later.'}
            </p>
          </div>

          <div className="bg-white shadow rounded-lg p-6">
            {!createdQuizId ? (
              <QuizForm onSubmit={handleSubmit} isSubmitting={isSubmitting} />
            ) : (
              <>
                <div className="mb-6 p-4 bg-blue-50 rounded-lg">
                  <p className="text-blue-800">
                    Quiz created successfully! Now add your questions below.
                  </p>
                  <div className="mt-2 flex justify-end space-x-3">
                    <button
                      onClick={() => router.push('/quizzes')}
                      className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50"
                    >
                      Finish Later
                    </button>
                    <button
                      onClick={() => router.push(`/quizzes/${createdQuizId}/questions`)}
                      className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700"
                    >
                      Manage Questions
                    </button>
                  </div>
                </div>
                <QuestionBank quizId={createdQuizId} />
              </>
            )}
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
};

export default CreateQuizPage;