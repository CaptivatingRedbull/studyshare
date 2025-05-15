import { apiClient } from "./apiClient";
import type { Review, ReviewCreateRequest, ReviewUpdateRequest } from "../lib/types";

/**
 * Fetches all reviews for a specific content item.
 * @param {number} contentId - The ID of the content.
 * @returns {Promise<Review[]>} A promise that resolves to an array of reviews.
 */
export const getAllReviewsForContent = async (contentId: number): Promise<Review[]> => {
  const response = await apiClient.get<Review[]>(`/api/contents/${contentId}/reviews`);
  return response.data;
};

/**
 * Fetches a specific review by its ID for a given content item.
 * @param {number} contentId - The ID of the content.
 * @param {number} reviewId - The ID of the review to fetch.
 * @returns {Promise<Review>} A promise that resolves to the review.
 */
export const getReviewById = async (contentId: number, reviewId: number): Promise<Review> => {
  const response = await apiClient.get<Review>(`/api/contents/${contentId}/reviews/${reviewId}`);
  return response.data;
};

/**
 * Creates a new review for a specific content item.
 * @param {number} contentId - The ID of the content to review.
 * @param {ReviewCreateRequest} reviewData - The data for the new review.
 * @returns {Promise<Review>} A promise that resolves to the created review.
 */
export const createReview = async (contentId: number, reviewData: ReviewCreateRequest): Promise<Review> => {
  const response = await apiClient.post<Review>(`/api/contents/${contentId}/reviews`, reviewData);
  return response.data;
};

/**
 * Updates an existing review. (Owner or Admin)
 * @param {number} contentId - The ID of the content the review belongs to.
 * @param {number} reviewId - The ID of the review to update.
 * @param {ReviewUpdateRequest} reviewData - The updated data for the review.
 * @returns {Promise<Review>} A promise that resolves to the updated review.
 */
export const updateReview = async (contentId: number, reviewId: number, reviewData: ReviewUpdateRequest): Promise<Review> => {
  const response = await apiClient.put<Review>(`/api/contents/${contentId}/reviews/${reviewId}`, reviewData);
  return response.data;
};

/**
 * Deletes a review. (Owner or Admin)
 * @param {number} contentId - The ID of the content the review belongs to.
 * @param {number} reviewId - The ID of the review to delete.
 * @returns {Promise<void>} A promise that resolves when the review is deleted.
 */
export const deleteReview = async (contentId: number, reviewId: number): Promise<void> => {
  await apiClient.delete(`/api/contents/${contentId}/reviews/${reviewId}`);
};
