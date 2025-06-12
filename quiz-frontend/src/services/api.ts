import axios from 'axios';

const api = axios.create({
  baseURL: 'https://quiz-app-backend-nm34.onrender.com', // Your deployed Spring Boot backend URL
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
}, (error) => {
  // Add error handling for the request
  return Promise.reject(error);
});

// Add a response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // If we get an auth error, clear the token and user data
      console.log('Authentication error:', error.response.status);
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      
      // You could redirect to login here or handle it in your components
      if (window.location.pathname !== '/auth/login') {
        window.location.href = '/auth/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;