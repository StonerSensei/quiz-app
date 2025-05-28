import { QuizDTO } from '../../types/quiz';
import Link from 'next/link';
import { toggleQuizActiveStatus, deleteQuiz } from '../../services/quiz';
import { useState } from 'react';
import { PencilAltIcon } from '@heroicons/react/outline';


interface QuizCardProps {
  quiz: QuizDTO;
  onUpdate: () => void;
  showActions?: boolean;
}

const QuizCard = ({ quiz, onUpdate, showActions = true }: QuizCardProps) => {
  const [isDeleting, setIsDeleting] = useState(false);
  const [isToggling, setIsToggling] = useState(false);

  const handleToggle = async () => {
    try {
      setIsToggling(true);
      await toggleQuizActiveStatus(quiz.id!);
      onUpdate();
    } catch (error) {
      console.error('Failed to toggle quiz status:', error);
      alert('Failed to toggle quiz status. Please try again.');
    } finally {
      setIsToggling(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this quiz? This action cannot be undone.')) {
      try {
        setIsDeleting(true);
        await deleteQuiz(quiz.id!);
        onUpdate();
      } catch (error) {
        console.error('Failed to delete quiz:', error);
        alert('Failed to delete quiz. Please try again.');
      } finally {
        setIsDeleting(false);
      }
    }
  };

  return (
    <div className="bg-white shadow rounded-lg overflow-hidden">
      <div className="p-5">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-lg font-semibold text-gray-900">{quiz.title}</h3>
            <p className="text-sm text-gray-500">Code: {quiz.code}</p>
          </div>
          <span
            className={`px-2 py-1 text-xs font-semibold rounded-full ${
              quiz.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
            }`}
          >
            {quiz.active ? 'Active' : 'Inactive'}
          </span>
        </div>
        <p className="mt-2 text-sm text-gray-600 line-clamp-2">{quiz.description}</p>
        <div className="mt-4 flex justify-between items-center">
          <div className="text-sm text-gray-500">
            <span>{quiz.numberOfQuestions} Questions</span> | <span>{quiz.maxMarks} Marks</span>
          </div>
        </div>
      </div>
      {showActions && (
        <div className="bg-gray-50 px-5 py-3 sm:flex sm:flex-wrap sm:justify-end gap-2">
          <Link
            href={`/quizzes/${quiz.id}`}
            className="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            View Details
          </Link>
          
          {/* Add the Manage Questions button here */}
          <Link
            href={`/quizzes/${quiz.id}/questions`}
            className="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
          >
            <PencilAltIcon className="mr-1 h-4 w-4" />
            Manage Questions
          </Link>
          
          <Link
            href={`/quizzes/edit/${quiz.id}`}
            className="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Edit
          </Link>
          <button
            type="button"
            onClick={handleToggle}
            disabled={isToggling}
            className="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
          >
            {isToggling ? 'Updating...' : quiz.active ? 'Deactivate' : 'Activate'}
          </button>
          <button
            type="button"
            onClick={handleDelete}
            disabled={isDeleting}
            className="w-full sm:w-auto inline-flex justify-center items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50"
          >
            {isDeleting ? 'Deleting...' : 'Delete'}
          </button>
        </div>
      )}
    </div>
  );
};

export default QuizCard;