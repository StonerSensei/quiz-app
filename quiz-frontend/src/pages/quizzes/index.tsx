import { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import Link from 'next/link';
import { getMyQuizzes, getAllQuizzes } from '../../services/quiz';
import QuizCard from '../../components/Quiz/QuizCard';
import ProtectedRoute from '../../components/ProtectedRoute';
import { useAuth } from '../../hooks/useAuth';

const QuizzesPage = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [filter, setFilter] = useState('my'); // 'my' or 'all'

  const isAdmin = user?.role === 'ADMIN';
  const canManageQuizzes = user?.role === 'ADMIN' || user?.role === 'TEACHER';

  // Fetch quizzes based on filter
  const { data: quizzes, isLoading, error } = useQuery({
    queryKey: ['quizzes', filter],
    queryFn: () => (filter === 'my' || !isAdmin ? getMyQuizzes() : getAllQuizzes()),
    enabled: !!user && canManageQuizzes,
  });

  const refreshQuizzes = () => {
    queryClient.invalidateQueries({ queryKey: ['quizzes'] });
  };

  if (!canManageQuizzes) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
        <div className="text-center p-5 bg-white rounded shadow-md">
          <h1 className="text-xl font-bold text-red-600">Access Denied</h1>
          <p className="mt-2">You do not have permission to manage quizzes.</p>
          <Link href="/dashboard" className="mt-4 text-indigo-600 hover:text-indigo-800">
            Return to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
            <h1 className="text-2xl font-bold text-gray-900 mb-4 sm:mb-0">Quiz Management</h1>
            <div className="flex flex-col sm:flex-row space-y-3 sm:space-y-0 sm:space-x-3">
              {isAdmin && (
                <div className="relative inline-block text-left">
                  <select
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                    className="block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
                  >
                    <option value="my">My Quizzes</option>
                    <option value="all">All Quizzes</option>
                  </select>
                </div>
              )}

              <Link
                href="/quizzes/create"
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Create New Quiz
              </Link>
            </div>
          </div>

          {isLoading ? (
            <div className="flex justify-center py-10">
              <div className="text-center">
                <p className="text-gray-500">Loading quizzes...</p>
              </div>
            </div>
          ) : error ? (
            <div className="bg-red-50 border-l-4 border-red-400 p-4 my-4">
              <div className="flex">
                <div className="flex-shrink-0">
                  <svg className="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                  </svg>
                </div>
                <div className="ml-3">
                  <p className="text-sm text-red-700">
                    Failed to load quizzes. Please refresh and try again.
                  </p>
                </div>
              </div>
            </div>
          ) : quizzes && quizzes.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {quizzes.map((quiz) => (
                <QuizCard key={quiz.id} quiz={quiz} onUpdate={refreshQuizzes} />
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center py-12">
              <svg
                className="mx-auto h-12 w-12 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
                />
              </svg>
              <h3 className="mt-2 text-sm font-medium text-gray-900">No quizzes found</h3>
              <p className="mt-1 text-sm text-gray-500">
                Get started by creating a new quiz.
              </p>
              <div className="mt-6">
                <Link
                  href="/quizzes/create"
                  className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                  Create New Quiz
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </ProtectedRoute>
  );
};

export default QuizzesPage;