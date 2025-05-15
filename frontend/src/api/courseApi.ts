import { apiClient } from "./apiClient";
import type { Course, CourseCreateRequest, CourseUpdateRequest } from "../lib/types";

const COURSES_BASE_URL = "/api/courses";

/**
 * Fetches all courses.
 * @returns {Promise<Course[]>} A promise that resolves to an array of courses.
 */
export const getAllCourses = async (): Promise<Course[]> => {
  const response = await apiClient.get<Course[]>(COURSES_BASE_URL);
  return response.data;
};

/**
 * Fetches a course by its ID.
 * @param {number} id - The ID of the course to fetch.
 * @returns {Promise<Course>} A promise that resolves to the course.
 */
export const getCourseById = async (id: number): Promise<Course> => {
  const response = await apiClient.get<Course>(`${COURSES_BASE_URL}/${id}`);
  return response.data;
};

/**
 * Creates a new course. (Admin only)
 * @param {CourseCreateRequest} courseData - The data for the new course.
 * @returns {Promise<Course>} A promise that resolves to the created course.
 */
export const createCourse = async (courseData: CourseCreateRequest): Promise<Course> => {
  const response = await apiClient.post<Course>(COURSES_BASE_URL, courseData);
  return response.data;
};

/**
 * Updates an existing course. (Admin only)
 * @param {number} id - The ID of the course to update.
 * @param {CourseUpdateRequest} courseData - The updated data for the course.
 * @returns {Promise<Course>} A promise that resolves to the updated course.
 */
export const updateCourse = async (id: number, courseData: CourseUpdateRequest): Promise<Course> => {
  const response = await apiClient.put<Course>(`${COURSES_BASE_URL}/${id}`, courseData);
  return response.data;
};

/**
 * Deletes a course. (Admin only)
 * @param {number} id - The ID of the course to delete.
 * @returns {Promise<void>} A promise that resolves when the course is deleted.
 */
export const deleteCourse = async (id: number): Promise<void> => {
  await apiClient.delete(`${COURSES_BASE_URL}/${id}`);
};

/**
 * Adds a lecturer to a course. (Admin only)
 * @param {number} courseId - The ID of the course.
 * @param {number} lecturerId - The ID of the lecturer to add.
 * @returns {Promise<Course>} A promise that resolves to the updated course.
 */
export const addLecturerToCourse = async (courseId: number, lecturerId: number): Promise<Course> => {
  const response = await apiClient.post<Course>(`${COURSES_BASE_URL}/${courseId}/lecturers/${lecturerId}`);
  return response.data;
};

/**
 * Removes a lecturer from a course. (Admin only)
 * @param {number} courseId - The ID of the course.
 * @param {number} lecturerId - The ID of the lecturer to remove.
 * @returns {Promise<Course>} A promise that resolves to the updated course.
 */
export const removeLecturerFromCourse = async (courseId: number, lecturerId: number): Promise<Course> => {
  const response = await apiClient.delete<Course>(`${COURSES_BASE_URL}/${courseId}/lecturers/${lecturerId}`);
  return response.data;
};
