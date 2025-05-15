import React, { createContext, useState, useContext, useEffect, ReactNode } from 'react';
import type { UserResponse, LoginResponse } from '../lib/types'; // Assuming UserResponse includes username and other details
import { getUserByUsername } from '@/api/userApi';

interface AuthContextType {
  isAuthenticated: boolean;
  user: UserResponse | null;
  token: string | null;
  login: (data: LoginResponse) => void;
  logout: () => void;
  isLoading: boolean; // To check if auth state is being loaded
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<UserResponse | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true); // Start with loading true

  useEffect(() => {
    // Check for token in session storage on initial load
    const storedToken = sessionStorage.getItem("jwt");
    const storedUser = sessionStorage.getItem("user"); // Assuming you might store basic user info

    if (storedToken) {
      setToken(storedToken);
      setIsAuthenticated(true);
      if (storedUser) {
        try {
          setUser(JSON.parse(storedUser));
        } catch (e) {
          console.error("Failed to parse stored user data:", e);
          sessionStorage.removeItem("user"); // Clear invalid user data
        }
      }
    }
    setIsLoading(false); // Finished loading auth state
  }, []);

  const loginContext = async (data: LoginResponse) => {
  sessionStorage.setItem("jwt", data.token);

  try {
    // Fetch user details from backend
    const userDetails = await getUserByUsername(data.username);
    sessionStorage.setItem("user", JSON.stringify(userDetails));
    setUser(userDetails);
  } catch (e) {
    // Fallback if fetch fails
    const fallbackUser: UserResponse = { username: data.username, id: 0, firstName: '', lastName: '', email: '', role: 'STUDENT' };
    sessionStorage.setItem("user", JSON.stringify(fallbackUser));
    setUser(fallbackUser);
    console.error("Failed to fetch user details:", e);
  }

  setToken(data.token);
  setIsAuthenticated(true);
};

  const logoutContext = () => {
    sessionStorage.removeItem("jwt");
    sessionStorage.removeItem("user");
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, token, login: loginContext, logout: logoutContext, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};