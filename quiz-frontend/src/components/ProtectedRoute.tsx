import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
const ProtectedRoute = ({ children, requiredRole }: { children: React.ReactNode; requiredRole?: string }) => {
  const { user, loading } = useAuth();
  const router = useRouter();
  useEffect(() => {
    if (!loading && !user) {
      router.push('/auth/login');
    } else if (!loading && user && requiredRole && user.role !== requiredRole) {
      router.push('/');
    }
  }, [user, loading, router, requiredRole]);
  if (loading || !user || (requiredRole && user.role !== requiredRole)) {
    return <div>Loading...</div>;
  }
  return <>{children}</>;
};
export default ProtectedRoute;