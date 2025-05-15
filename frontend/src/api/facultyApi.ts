import { apiClient } from "./apiClient";
import type { Faculty, FacultyCreateRequest, FacultyUpdateRequest } from "../lib/types";

const FACULTIES_BASE_URL = "/api/faculties";

/**
 * Fetches all faculties.
 * @returns {Promise<Faculty[]>} A promise that resolves to an array of faculties.
 */
export const getAllFaculties = async (): Promise<Faculty[]> => {
  const response = await apiClient.get<Faculty[]>(FACULTIES_BASE_URL);
  return response.data;
};

/**
 * Fetches a faculty by its ID.
 * @param {number} id - The ID of the faculty to fetch.
 * @returns {Promise<Faculty>} A promise that resolves to the faculty.
 */
export const getFacultyById = async (id: number): Promise<Faculty> => {
  const response = await apiClient.get<Faculty>(`${FACULTIES_BASE_URL}/${id}`);
  return response.data;
};

/**
 * Creates a new faculty. (Admin only)
 * @param {FacultyCreateRequest} facultyData - The data for the new faculty.
 * @returns {Promise<Faculty>} A promise that resolves to the created faculty.
 */
export const createFaculty = async (facultyData: FacultyCreateRequest): Promise<Faculty> => {
  const response = await apiClient.post<Faculty>(FACULTIES_BASE_URL, facultyData);
  return response.data;
};

/**
 * Updates an existing faculty. (Admin only)
 * @param {number} id - The ID of the faculty to update.
 * @param {FacultyUpdateRequest} facultyData - The updated data for the faculty.
 * @returns {Promise<Faculty>} A promise that resolves to the updated faculty.
 */
export const updateFaculty = async (id: number, facultyData: FacultyUpdateRequest): Promise<Faculty> => {
  const response = await apiClient.put<Faculty>(`${FACULTIES_BASE_URL}/${id}`, facultyData);
  return response.data;
};

/**
 * Deletes a faculty. (Admin only)
 * @param {number} id - The ID of the faculty to delete.
 * @returns {Promise<void>} A promise that resolves when the faculty is deleted.
 */
export const deleteFaculty = async (id: number): Promise<void> => {
  await apiClient.delete(`${FACULTIES_BASE_URL}/${id}`);
};
