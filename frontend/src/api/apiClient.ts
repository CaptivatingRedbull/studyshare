import axios, { type AxiosRequestConfig, type AxiosInstance } from "axios";

// Retrieve the API base URL from environment variables
const API_BASE_URL = import.meta.env.VITE_API_URL;

if (!API_BASE_URL) {
  console.error("VITE_API_URL is not defined. Please check your .env file.");
}

/**
 * Creates an Axios instance with a base URL.
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
});

/**
 * Returns the Axios request configuration with the JWT token in the Authorization header.
 * @returns {AxiosRequestConfig} Axios request configuration.
 */
export const getAxiosConfig = (): AxiosRequestConfig => {
  const token = sessionStorage.getItem("jwt");
  const config: AxiosRequestConfig = {
    headers: {
      "Content-Type": "application/json",
    },
  };
  if (token) {
    if (config.headers) {
        config.headers["Authorization"] = `Bearer ${token}`;
    } else {
        config.headers = { "Authorization": `Bearer ${token}` };
    }
  }
  return config;
};

/**
 * Configures an interceptor to automatically add the JWT token to requests.
 * Also handles 401 errors globally by redirecting to login.
 */
apiClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("jwt");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    config.headers["Content-Type"] = "application/json";
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token might be invalid or expired
      sessionStorage.removeItem("jwt");
      // Redirect to login page, or show a modal, etc.
      // Ensure this doesn't cause redirect loops if the login page itself makes API calls.
      if (window.location.pathname !== "/login") {
         // window.location.href = "/login";
         console.warn("Unauthorized request or token expired. Redirecting to login is typically handled by the application's routing logic based on auth state.");
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;