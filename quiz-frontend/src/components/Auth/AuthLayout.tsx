import { ReactNode } from 'react';
import Link from 'next/link';

const AuthLayout = ({
  children,
  title,
  linkText,
  linkHref,
}: {
  children: ReactNode;
  title: string;
  linkText: string;
  linkHref: string;
}) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            {title}
          </h2>
        </div>
        {children}
        <div className="text-center">
          <Link
            href={linkHref}
            className="font-medium text-indigo-600 hover:text-indigo-500"
          >
            {linkText}
          </Link>
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;
