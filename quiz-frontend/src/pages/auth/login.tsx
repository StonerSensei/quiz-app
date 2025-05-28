import AuthLayout from '../../components/Auth/AuthLayout';
import LoginForm from '../../components/Auth/LoginForm';

const LoginPage = () => {
  return (
    <AuthLayout 
      title="Sign in to your account" 
      linkText="Don't have an account? Register here"
      linkHref="/auth/register"
    >
      <LoginForm />
    </AuthLayout>
  );
};

export default LoginPage;