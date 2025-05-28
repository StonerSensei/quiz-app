// src/types/auth.ts
export type UserRole = 'ADMIN' | 'TEACHER' | 'STUDENT';

export interface RegisterRequest {
  username: string;
  password: string;
  role: UserRole;
}

export interface AuthenticationRequest {
  username: string;
  password: string;
}

export interface AuthenticationResponse {
  token: string;
  username: string;
  role: UserRole;
}

export interface User {
  username: string;
  role: UserRole;
  token: string;
}