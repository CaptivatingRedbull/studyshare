import React, { createContext, useState, useContext, useEffect, ReactNode } from 'react';
import type { UserResponse, LoginResponse } from '../lib/types'; // Assuming UserResponse includes username and other details

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

  const loginContext = (data: LoginResponse) => {
    sessionStorage.setItem("jwt", data.token);
    // Potentially fetch user details here if not included in LoginResponse,
    // or if LoginResponse includes user details, store them.
    // For now, let's assume LoginResponse might include basic user info or we set it separately.
    // This example just sets a placeholder user based on username.
    const loggedInUser: UserResponse = { username: data.username, id: 0, firstName: '', lastName: '', email: '', role: 'STUDENT' }; // Adjust as per your UserResponse structure
    sessionStorage.setItem("user", JSON.stringify(loggedInUser));

    setToken(data.token);
    setUser(loggedInUser);
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