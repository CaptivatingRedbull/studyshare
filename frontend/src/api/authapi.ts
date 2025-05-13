import apiClient from "./apiClient";
import type { LoginRequest, LoginResponse, RegisterRequest } from "../lib/types";

const AUTH_BASE_URL = "/api/auth";

/**
 * Logs in a user.
 * @param {LoginRequest} loginData - The username and password for login.
 * @returns {Promise<LoginResponse>} A promise that resolves to the login response containing the token and username.
 */
export const login = async (loginData: LoginRequest): Promise<LoginResponse> => {
  const response = await apiClient.post<LoginResponse>(`${AUTH_BASE_URL}/login`, loginData);
  return response.data;
};

/**
 * Registers a new user.
 * @param {RegisterRequest} registerData - The user details for registration.
 * @returns {Promise<LoginResponse>} A promise that resolves to the login response containing the token and username (backend logs in user upon successful registration).
 */
export const register = async (registerData: RegisterRequest): Promise<LoginResponse> => {
  const response = await apiClient.post<LoginResponse>(`${AUTH_BASE_URL}/register`, registerData);
  return response.data;
};
