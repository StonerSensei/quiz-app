import AuthLayout from '../../components/Auth/AuthLayout';
import RegisterForm from '../../components/Auth/RegisterForm';

const RegisterPage = () => {
  return (
    <AuthLayout 
      title="Create a new account" 
      linkText="Already have an account? Login here"
      linkHref="/auth/login"
    >
      <RegisterForm />
    </AuthLayout>
  );
};

export default RegisterPage;