import axios, { type AxiosRequestConfig, type AxiosInstance } from "axios";

const API_BASE_URL = import.meta.env.VITE_API_URL;

if (!API_BASE_URL) {
  console.error("VITE_API_URL is not defined. Please check your .env file.");
}

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
});

apiClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("jwt");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Conditionally set Content-Type
    // If config.data is FormData, Axios will set the correct multipart Content-Type with boundary
    // Otherwise, for typical JSON payloads, set it to application/json
    if (!(config.data instanceof FormData)) {
      config.headers["Content-Type"] = "application/json";
    }
    // If it IS FormData, let Axios handle the Content-Type.
    // You might even want to explicitly delete it if it was set by default elsewhere:
    // else {
    //   delete config.headers["Content-Type"];
    // }


    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// The getAxiosConfig function might also need similar conditional logic if used directly
// For now, the interceptor is the primary concern for calls made via apiClient.post, .get etc.
export const getAxiosConfig = (): AxiosRequestConfig => {
  const token = sessionStorage.getItem("jwt");
  const config: AxiosRequestConfig = {
    headers: {
      // Default Content-Type can be application/json here for general use,
      // but for FormData, it should be omitted or handled by Axios.
      // "Content-Type": "application/json", // Consider removing or making conditional
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


// Rest of your interceptors (response) can remain the same
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      sessionStorage.removeItem("jwt");
      if (window.location.pathname !== "/login") {
         console.warn("Unauthorized request or token expired. Redirecting to login is typically handled by the application's routing logic based on auth state.");
      }
    }
    return Promise.reject(error);
  }
);