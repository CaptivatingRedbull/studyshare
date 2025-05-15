import { apiClient } from "./apiClient";
import type { Lecturer, LecturerCreateRequest, LecturerUpdateRequest } from "../lib/types";

const LECTURERS_BASE_URL = "/api/lecturers";

/**
 * Fetches all lecturers.
 * @returns {Promise<Lecturer[]>} A promise that resolves to an array of lecturers.
 */
export const getAllLecturers = async (): Promise<Lecturer[]> => {
  const response = await apiClient.get<Lecturer[]>(LECTURERS_BASE_URL);
  return response.data;
};

/**
 * Fetches a lecturer by their ID.
 * @param {number} id - The ID of the lecturer to fetch.
 * @returns {Promise<Lecturer>} A promise that resolves to the lecturer.
 */
export const getLecturerById = async (id: number): Promise<Lecturer> => {
  const response = await apiClient.get<Lecturer>(`${LECTURERS_BASE_URL}/${id}`);
  return response.data;
};

/**
 * Creates a new lecturer. (Admin only)
 * @param {LecturerCreateRequest} lecturerData - The data for the new lecturer.
 * @returns {Promise<Lecturer>} A promise that resolves to the created lecturer.
 */
export const createLecturer = async (lecturerData: LecturerCreateRequest): Promise<Lecturer> => {
  const response = await apiClient.post<Lecturer>(LECTURERS_BASE_URL, lecturerData);
  return response.data;
};

/**
 * Updates an existing lecturer. (Admin only)
 * @param {number} id - The ID of the lecturer to update.
 * @param {LecturerUpdateRequest} lecturerData - The updated data for the lecturer.
 * @returns {Promise<Lecturer>} A promise that resolves to the updated lecturer.
 */
export const updateLecturer = async (id: number, lecturerData: LecturerUpdateRequest): Promise<Lecturer> => {
  const response = await apiClient.put<Lecturer>(`${LECTURERS_BASE_URL}/${id}`, lecturerData);
  return response.data;
};

/**
 * Deletes a lecturer. (Admin only)
 * @param {number} id - The ID of the lecturer to delete.
 * @returns {Promise<void>} A promise that resolves when the lecturer is deleted.
 */
export const deleteLecturer = async (id: number): Promise<void> => {
  await apiClient.delete(`${LECTURERS_BASE_URL}/${id}`);
};
