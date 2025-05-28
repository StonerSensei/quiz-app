import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '../hooks/useAuth';

export default function HomePage() {
  const router = useRouter();
  const { user, loading } = useAuth();

  useEffect(() => {
    if (!loading) {
      if (user) {
        // If user is logged in, redirect to dashboard
        router.push('/dashboard');
      } else {
        // If user is not logged in, redirect to login page
        router.push('/auth/login');
      }
    }
  }, [user, loading, router]);

  // Show loading state while checking auth status
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="text-center">
        <h2 className="text-xl font-semibold text-gray-800">Loading...</h2>
        <p className="mt-2 text-gray-600">Please wait while we redirect you...</p>
      </div>
    </div>
  );
}