import { useRouter } from 'next/router';
import Link from 'next/link';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { getQuizById, toggleQuizActiveStatus, deleteQuiz} from '../../services/quiz';
import { getQuestionsWithAnswersByQuizId } from '@/services/question';
import ProtectedRoute from '../../components/ProtectedRoute';
import { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';

const QuizDetailPage = () => {
  const router = useRouter();
  const { id } = router.query;
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const [isDeleting, setIsDeleting] = useState(false);
  const [isToggling, setIsToggling] = useState(false);

  // Fetch quiz data
  const { data: quiz, isLoading, error } = useQuery({
    queryKey: ['quiz', id],
    queryFn: () => getQuizById(Number(id)),
    enabled: !!id,
  });

  // Fetch questions data
  const { data: questions } = useQuery({
    queryKey: ['quizQuestions', id],
    queryFn: () => getQuestionsWithAnswersByQuizId(Number(id)),
    enabled: !!id,
  });

  const canManageQuiz = user?.role === 'ADMIN' || user?.role === 'TEACHER';

  const handleToggleActive = async () => {
    if (!id) return;
    
    try {
      setIsToggling(true);
      await toggleQuizActiveStatus(Number(id));
      queryClient.invalidateQueries({ queryKey: ['quiz', id] });
    } catch (error) {
      console.error('Failed to toggle quiz status:', error);
      alert('Failed to toggle quiz status. Please try again.');
    } finally {
      setIsToggling(false);
    }
  };

  const handleDelete = async () => {
    if (!id) return;
    
    if (window.confirm('Are you sure you want to delete this quiz? This action cannot be undone.')) {
      try {
        setIsDeleting(true);
        await deleteQuiz(Number(id));
        router.push('/quizzes');
      } catch (error) {
        console.error('Failed to delete quiz:', error);
        alert('Failed to delete quiz. Please try again.');
      } finally {
        setIsDeleting(false);
      }
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

  if (error || !quiz) {
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
    <ProtectedRoute>
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
            <div className="mt-4 flex flex-col sm:flex-row sm:items-center sm:justify-between">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">{quiz.title}</h1>
                <div className="mt-1 flex items-center">
                  <span className="text-sm text-gray-500">Quiz Code: {quiz.code}</span>
                  <span
                    className={`ml-3 px-2 py-1 text-xs font-semibold rounded-full ${
                      quiz.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {quiz.active ? 'Active' : 'Inactive'}
                  </span>
                </div>
              </div>
              {canManageQuiz && (
                <div className="mt-4 sm:mt-0 flex space-x-3">
                  <Link
                    href={`/quizzes/edit/${quiz.id}`}
                    className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    Edit
                  </Link>
                  <button
                    type="button"
                    onClick={handleToggleActive}
                    disabled={isToggling}
                    className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
                  >
                    {isToggling ? 'Updating...' : quiz.active ? 'Deactivate' : 'Activate'}
                  </button>
                  <button
                    type="button"
                    onClick={handleDelete}
                    disabled={isDeleting}
                    className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50"
                  >
                    {isDeleting ? 'Deleting...' : 'Delete'}
                  </button>
                </div>
              )}
            </div>
          </div>

          <div className="bg-white shadow overflow-hidden sm:rounded-lg">
            <div className="px-4 py-5 sm:px-6">
              <h3 className="text-lg leading-6 font-medium text-gray-900">Quiz Details</h3>
              <p className="mt-1 max-w-2xl text-sm text-gray-500">Information about the quiz.</p>
            </div>
            <div className="border-t border-gray-200">
              <dl>
                <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Quiz Title</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{quiz.title}</dd>
                </div>
                <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Quiz Code</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{quiz.code}</dd>
                </div>
                <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Description</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{quiz.description}</dd>
                </div>
                <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Maximum Marks</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{quiz.maxMarks}</dd>
                </div>
                <div className="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Number of Questions</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                    {questions ? `${questions.length} of ${quiz.numberOfQuestions}` : quiz.numberOfQuestions}
                  </dd>
                </div>
                <div className="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt className="text-sm font-medium text-gray-500">Status</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                    <span
                      className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        quiz.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}
                    >
                      {quiz.active ? 'Active' : 'Inactive'}
                    </span>
                  </dd>
                </div>
              </dl>
            </div>
          </div>

          {/* Questions Section */}
          <div className="mt-8 bg-white shadow overflow-hidden sm:rounded-lg">
            <div className="px-4 py-5 sm:px-6 border-b border-gray-200">
              <div className="flex justify-between items-center">
                <h3 className="text-lg leading-6 font-medium text-gray-900">
                  Questions {questions && `(${questions.length})`}
                </h3>
                {canManageQuiz && (
                  <Link
                    href={`/quizzes/${quiz.id}/question`}
                    className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700"
                  >
                    Manage Questions
                  </Link>
                )}
              </div>
            </div>

            {questions ? (
              questions.length > 0 ? (
                <div className="divide-y divide-gray-200">
                  {questions.map((question, index) => (
                    <div key={question.id} className="p-6">
                      <div className="flex justify-between items-start">
                        <h4 className="font-medium">
                          Q{index + 1}: {question.content}
                        </h4>
                      </div>
                      {question.image && (
                        <img
                          src={question.image}
                          alt="Question illustration"
                          className="mt-2 max-w-full h-auto rounded-md"
                        />
                      )}
                      <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-3">
                        {['option1', 'option2', 'option3', 'option4'].map((option) => (
                          <div
                            key={option}
                            className={`p-3 rounded-md ${
                              question.answer === option
                                ? 'bg-green-50 border border-green-200'
                                : 'bg-gray-50'
                            }`}
                          >
                            <span className="font-medium mr-2">
                              {option.replace('option', '').toUpperCase()}:
                            </span>
                            {question[option as keyof typeof question]}
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="p-6 text-center text-gray-500">
                  No questions added yet
                </div>
              )
            ) : (
              <div className="p-6 text-center text-gray-500">
                Loading questions...
              </div>
            )}
          </div>
        </div>
      </div>
    </ProtectedRoute>
  );
};

export default QuizDetailPage;