import apiClient from "./apiClient";
import type { UserResponse, UserUpdateRequest } from "../lib/types";

const USERS_BASE_URL = "/api/users";

/**
 * Fetches all users. (Admin only)
 * @returns {Promise<UserResponse[]>} A promise that resolves to an array of user responses.
 */
export const getAllUsers = async (): Promise<UserResponse[]> => {
  const response = await apiClient.get<UserResponse[]>(USERS_BASE_URL);
  return response.data;
};

/**
 * Fetches a user by their ID. (Admin only)
 * @param {number} id - The ID of the user to fetch.
 * @returns {Promise<UserResponse>} A promise that resolves to the user response.
 */
export const getUserById = async (id: number): Promise<UserResponse> => {
  const response = await apiClient.get<UserResponse>(`${USERS_BASE_URL}/${id}`);
  return response.data;
};

/**
 * Fetches a user by their username. (Admin only)
 * @param {string} username - The username of the user to fetch.
 * @returns {Promise<UserResponse>} A promise that resolves to the user response.
 */
export const getUserByUsername = async (username: string): Promise<UserResponse> => {
  const response = await apiClient.get<UserResponse>(`${USERS_BASE_URL}/byUsername`, { params: { username } });
  return response.data;
};

/**
 * Updates a user. (Admin only)
 * @param {number} id - The ID of the user to update.
 * @param {UserUpdateRequest} userData - The user data to update.
 * @returns {Promise<UserResponse>} A promise that resolves to the updated user response.
 */
export const updateUser = async (id: number, userData: UserUpdateRequest): Promise<UserResponse> => {
  const response = await apiClient.put<UserResponse>(`${USERS_BASE_URL}/${id}`, userData);
  return response.data;
};

/**
 * Deletes a user. (Admin only)
 * @param {number} id - The ID of the user to delete.
 * @returns {Promise<void>} A promise that resolves when the user is deleted.
 */
export const deleteUser = async (id: number): Promise<void> => {
  await apiClient.delete(`${USERS_BASE_URL}/${id}`);
};