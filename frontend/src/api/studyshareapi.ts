import axios, { AxiosRequestConfig } from "axios";
import { UserResponse, User, UserEntry } from "../lib/types";

const getAxiosConfig = (): AxiosRequestConfig => {
    const token = sessionStorage.getItem("jwt");
    return {
        headers: {
            "Content-Type": "application/json",
            "Authorization": token 
        },
    };
};

export const fetchUsers = async () => {
    //const token = sessionStorage.getItem("jwt");
    const response = await axios.get(`${import.meta.env.VITE_API_URL}/users`, getAxiosConfig());
    //console.log("Response data:", response.data); // Log the response data
    return response.data; // Ensure this returns an array
};

export const createUser = async (user: User): Promise<UserResponse> => {
    const response = await axios.post(`${import.meta.env.VITE_API_URL}/users`, user, getAxiosConfig());
    return response.data;
};

export const deleteCar = async (link: string): Promise<void> => {
    //const token = sessionStorage.getItem("jwt");
    await axios.delete(`${import.meta.env.VITE_API_URL}/users/` + link, getAxiosConfig()); // We don't need to return anything, as it's just a deletion
};

export const addCar = async (user: User): Promise<UserResponse> => {
    //const token = sessionStorage.getItem("jwt");
    const response = await axios.post(`${import.meta.env.VITE_API_URL}/cars`, user, getAxiosConfig());
    return response.data;
}

export const updateCar = async (userEntry: UserEntry): Promise<UserResponse> => {
    //const token = sessionStorage.getItem("jwt");
    const response = await axios.put(`${import.meta.env.VITE_API_URL}/cars/` + userEntry.url, userEntry.user, getAxiosConfig());
    return response.data;
}
