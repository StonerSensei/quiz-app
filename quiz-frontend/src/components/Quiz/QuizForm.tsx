import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { QuizDTO, QuizFormData } from '../../types/quiz';
const schema: z.ZodType<QuizFormData> = z.object({
  title: z.string().min(1, 'Title is required'),code: z.string().optional(),description: z.string().min(1, 'Description is required'),
  maxMarks: z.number().int().positive('Max marks must be a positive integer'),numberOfQuestions: z.number().int().positive('Number of questions must be a positive integer'),
  active: z.preprocess((val) => val ?? false, z.boolean()) as z.ZodType<boolean, z.ZodTypeDef, boolean>
});
interface QuizFormProps {
  initialData?: QuizDTO;onSubmit: (data: QuizFormData) => void;isSubmitting: boolean;
}
const QuizForm = ({ initialData, onSubmit, isSubmitting }: QuizFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<QuizFormData>({
    resolver: zodResolver(schema),
    defaultValues: initialData
      ? {
          title: initialData.title,code: initialData.code,description: initialData.description,maxMarks: initialData.maxMarks,
          numberOfQuestions: initialData.numberOfQuestions,active: initialData.active,
        }
      : {
          title: '',code: '',description: '',maxMarks: 100,numberOfQuestions: 10,active: false,
        },
  });
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label htmlFor="title" className="block text-sm font-medium text-gray-700">
          Title *
        </label>
        <input
          id="title"
          type="text"
          {...register('title')}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        />
        {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title.message}</p>}
      </div>

      <div>
        <label htmlFor="code" className="block text-sm font-medium text-gray-700">
          Code (Optional - will be generated if not provided)
        </label>
        <input
          id="code"
          type="text"
          {...register('code')}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        />
        {errors.code && <p className="mt-1 text-sm text-red-600">{errors.code.message}</p>}
      </div>

      <div>
        <label htmlFor="description" className="block text-sm font-medium text-gray-700">
          Description *
        </label>
        <textarea
          id="description"
          rows={3}
          {...register('description')}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        />
        {errors.description && <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label htmlFor="maxMarks" className="block text-sm font-medium text-gray-700">
            Max Marks *
          </label>
          <input
            id="maxMarks"
            type="number"
            min="1"
            {...register('maxMarks', { valueAsNumber: true })}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          />
          {errors.maxMarks && <p className="mt-1 text-sm text-red-600">{errors.maxMarks.message}</p>}
        </div>

        <div>
          <label htmlFor="numberOfQuestions" className="block text-sm font-medium text-gray-700">
            Number of Questions *
          </label>
          <input
            id="numberOfQuestions"
            type="number"
            min="1"
            {...register('numberOfQuestions', { valueAsNumber: true })}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          />
          {errors.numberOfQuestions && (
            <p className="mt-1 text-sm text-red-600">{errors.numberOfQuestions.message}</p>
          )}
        </div>
      </div>

      <div className="flex items-center">
        <input
          id="active"
          type="checkbox"
          {...register('active')}
          className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
        />
        <label htmlFor="active" className="ml-2 block text-sm text-gray-900">
          Active (make quiz available to students)
        </label>
      </div>

      <div className="flex justify-end">
        <button
          type="submit"
          disabled={isSubmitting}
          className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
        >
          {isSubmitting ? 'Saving...' : initialData ? 'Update Quiz' : 'Create Quiz'}
        </button>
      </div>
    </form>
  );
};
export default QuizForm;