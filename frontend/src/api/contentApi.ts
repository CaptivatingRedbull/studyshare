import { apiClient } from "./apiClient";
import type { Content, ContentCreateRequest, ContentUpdateRequest } from "../lib/types";
import type { ContentCategory } from "../lib/types";

const CONTENTS_BASE_URL = "/api/contents";

/**
 * Fetches all content.
 * @returns {Promise<Content[]>} A promise that resolves to an array of content.
 */
export const getAllContents = async (): Promise<Content[]> => {
    const response = await apiClient.get<Content[]>(CONTENTS_BASE_URL);
    return response.data;
};

/**
 * Fetches content by its ID.
 * @param {number} id - The ID of the content to fetch.
 * @returns {Promise<Content>} A promise that resolves to the content.
 */
export const getContentById = async (id: number): Promise<Content> => {
    const response = await apiClient.get<Content>(`${CONTENTS_BASE_URL}/${id}`);
    return response.data;
};

/**
 * Creates new content.
 * @param {ContentCreateRequest} contentData - The data for the new content.
 * @returns {Promise<Content>} A promise that resolves to the created content.
 */
export const createContent = async (contentData: ContentCreateRequest): Promise<Content> => {
    const response = await apiClient.post<Content>(CONTENTS_BASE_URL, contentData);
    return response.data;
};

/**
 * Updates existing content. (Admin or Owner)
 * @param {number} id - The ID of the content to update.
 * @param {ContentUpdateRequest} contentData - The updated data for the content.
 * @returns {Promise<Content>} A promise that resolves to the updated content.
 */
export const updateContent = async (id: number, contentData: ContentUpdateRequest): Promise<Content> => {
    const response = await apiClient.put<Content>(`${CONTENTS_BASE_URL}/${id}`, contentData);
    return response.data;
};

/**
 * Deletes content. (Admin or Owner)
 * @param {number} id - The ID of the content to delete.
 * @returns {Promise<void>} A promise that resolves when the content is deleted.
 */
export const deleteContent = async (id: number): Promise<void> => {
    await apiClient.delete(`${CONTENTS_BASE_URL}/${id}`);
};

/**
 * Reports content.
 * @param {number} id - The ID of the content to report.
 * @returns {Promise<Content>} A promise that resolves to the updated content with incremented report count.
 */
export const reportContent = async (id: number): Promise<Content> => {
    const response = await apiClient.post<Content>(`${CONTENTS_BASE_URL}/${id}/report`);
    return response.data;
};

/**
 * Marks content as outdated.
 * @param {number} id - The ID of the content to mark as outdated.
 * @returns {Promise<Content>} A promise that resolves to the updated content with incremented outdated count.
 */
export const markContentAsOutdated = async (id: number): Promise<Content> => {
    const response = await apiClient.post<Content>(`${CONTENTS_BASE_URL}/${id}/mark-outdated`);
    return response.data;
};

export const browseContents = async (
    facultyId?: number,
    courseId?: number,
    lecturerId?: number,
    contentCategory?: ContentCategory,
    searchTerm?: String,
    sortBy?: "uploadDate" | "title" | "rating",
    sortDirection?: "desc" | "asc",
    page?: number,
    size?: number
): Promise<{
    content: Content[],
    totalElements: number,
    totalPages: number,
    currentPage: number
}> => {
    const params: Record<string, any> = {};
    if (facultyId !== undefined) params.facultyId = facultyId;
    if (courseId !== undefined) params.courseId = courseId;
    if (lecturerId !== undefined) params.lecturerId = lecturerId;
    if (contentCategory !== undefined) params.contentCategory = contentCategory;
    if (searchTerm !== undefined) params.searchTerm = searchTerm;
    if (sortBy !== undefined) params.sortBy = sortBy;
    if (sortDirection !== undefined) params.sortDirection = sortDirection;
    if (page !== undefined) params.page = page;
    if (size !== undefined) params.size = size;

    const response = await apiClient.get<{
        content: Content[],
        totalElements: number,
        totalPages: number,
        number: number
    }>(
        `${CONTENTS_BASE_URL}/browse`,
        { params }
    );
    return {
        content: response.data.content,
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        currentPage: response.data.number
    }
}