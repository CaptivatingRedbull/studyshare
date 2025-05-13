// --- User & Auth Types ---
export type Role = "STUDENT" | "ADMIN";

export interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  role: Role;
}

export interface LoginRequest {
  username: string;
  password: string; 
}

export interface LoginResponse {
  token: string;
  username: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  password: string; 
}

export interface UserCreateRequest {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  password: string; 
  role: Role;
}

export interface UserUpdateRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: Role;
}

// --- Faculty Types ---
export interface Faculty {
  id: number;
  name: string;
}

export interface FacultyCreateRequest {
  name: string;
}

export interface FacultyUpdateRequest {
  name: string;
}

// --- Lecturer Types ---
export interface Lecturer {
  id: number;
  name: string;
  email: string | null; // Email can be null in backend Lecturer entity
  courseIds: number[];
}

export interface LecturerCreateRequest {
  name: string;
  email?: string | null;
  courseIds?: number[];
}

export interface LecturerUpdateRequest {
  name?: string;
  email?: string | null;
  courseIds?: number[];
}

// --- Course Types ---
export interface Course {
  id: number;
  name: string;
  faculty: Faculty;
  lecturerIds: number[];
}

export interface CourseCreateRequest {
  name: string;
  facultyId: number;
  lecturerIds?: number[];
}

export interface CourseUpdateRequest {
  name?: string;
  facultyId?: number;
  lecturerIds?: number[];
}

// --- Content Types ---
export type ContentCategory = "PDF" | "IMAGE" | "ZIP";

export interface Content {
  id: number;
  uploadedBy: UserResponse;
  reportedCount: number;
  outdatedCount: number;
  uploadDate: string; // ISO date string e.g. "2023-10-27"
  contentCategory: ContentCategory;
  lecturer: Lecturer | null;
  course: Course;
  faculty: Faculty;
  filePath: string;
  title: string | null;
}

export interface ContentCreateRequest {
  contentCategory: ContentCategory;
  courseId: number;
  lecturerId?: number | null;
  facultyId: number;
  filePath: string;
  title?: string | null;
}

export interface ContentUpdateRequest {
  contentCategory?: ContentCategory;
  courseId?: number;
  lecturerId?: number | null;
  facultyId?: number;
  filePath?: string;
  title?: string | null;
}

// --- Review Types ---
export interface Review {
  id: number;
  stars: number;
  subject: string;
  comment: string | null;
  user: UserResponse;
  contentId: number;
  createdAt: string; // ISO date-time string e.g. "2023-10-27T10:30:00"
  updatedAt: string; // ISO date-time string
}

export interface ReviewCreateRequest {
  stars: number;
  subject: string;
  comment?: string | null;
}

export interface ReviewUpdateRequest {
  stars?: number;
  subject?: string;
  comment?: string | null;
}