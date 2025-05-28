import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { QuestionDTO, QuestionFormData } from '../../types/question';

const schema: z.ZodType<QuestionFormData> = z.object({
  content: z.string().min(1, 'Question content is required'),
  image: z.string().optional(),
  option1: z.string().min(1, 'Option 1 is required'),
  option2: z.string().min(1, 'Option 2 is required'),
  option3: z.string().min(1, 'Option 3 is required'),
  option4: z.string().min(1, 'Option 4 is required'),
  answer: z.string().min(1, 'Correct answer is required'),
});

interface QuestionFormProps {
  initialData?: QuestionDTO;
  onSubmit: (data: QuestionFormData) => void;
  isSubmitting: boolean;
}

const QuestionForm = ({ initialData, onSubmit, isSubmitting }: QuestionFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<QuestionFormData>({
    resolver: zodResolver(schema),
    defaultValues: initialData
      ? {
          content: initialData.content,
          image: initialData.image,
          option1: initialData.option1,
          option2: initialData.option2,
          option3: initialData.option3,
          option4: initialData.option4,
          answer: initialData.answer,
        }
      : {
          content: '',
          image: '',
          option1: '',
          option2: '',
          option3: '',
          option4: '',
          answer: '',
        },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label htmlFor="content" className="block text-sm font-medium text-gray-700">
          Question Content *
        </label>
        <textarea
          id="content"
          rows={3}
          {...register('content')}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        />
        {errors.content && <p className="mt-1 text-sm text-red-600">{errors.content.message}</p>}
      </div>

      <div>
        <label htmlFor="image" className="block text-sm font-medium text-gray-700">
          Image URL (Optional)
        </label>
        <input
          id="image"
          type="text"
          {...register('image')}
          className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        />
        {errors.image && <p className="mt-1 text-sm text-red-600">{errors.image.message}</p>}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label htmlFor="option1" className="block text-sm font-medium text-gray-700">
            Option 1 *
          </label>
          <input
            id="option1"
            type="text"
            {...register('option1')}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          />
          {errors.option1 && <p className="mt-1 text-sm text-red-600">{errors.option1.message}</p>}
        </div>

        <div>
          <label htmlFor="option2" className="block text-sm font-medium text-gray-700">
            Option 2 *
          </label>
          <input
            id="option2"
            type="text"
            {...register('option2')}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          />
          {errors.option2 && <p className="mt-1 text-sm text-red-600">{errors.option2.message}</p>}
        </div>

        <div>
          <label htmlFor="option3" className="block text-sm font-medium text-gray-700">
            Option 3 *
          </label>
          <input
            id="option3"
            type="text"
            {...register('option3')}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          />
          {errors.option3 && <p className="mt-1 text-sm text-red-600">{errors.option3.message}</p>}
        </div>

        <div>
          <label htmlFor="option4" className="block text-sm font-medium text-gray-700">
            Option 4 *
          </label>
          <input
            id="option4"
            type="text"
            {...register('option4')}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          />
          {errors.option4 && <p className="mt-1 text-sm text-red-600">{errors.option4.message}</p>}
        </div>
      </div>

      <div>
        <label htmlFor="answer" className="block text-sm font-medium text-gray-700">
          Correct Answer *
        </label>
        <select
          id="answer"
          {...register('answer')}
          className="mt-1 block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
        >
          <option value="">Select correct answer</option>
          <option value="option1">Option 1</option>
          <option value="option2">Option 2</option>
          <option value="option3">Option 3</option>
          <option value="option4">Option 4</option>
        </select>
        {errors.answer && <p className="mt-1 text-sm text-red-600">{errors.answer.message}</p>}
      </div>

      <div className="flex justify-end space-x-3">
        {initialData && (
          <button
            type="button"
            onClick={() => reset()}
            className="inline-flex justify-center py-2 px-4 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Reset
          </button>
        )}
        <button
          type="submit"
          disabled={isSubmitting}
          className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
        >
          {isSubmitting ? 'Saving...' : initialData ? 'Update Question' : 'Add Question'}
        </button>
      </div>
    </form>
  );
};

export default QuestionForm;