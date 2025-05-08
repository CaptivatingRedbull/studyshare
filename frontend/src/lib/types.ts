export type UserResponse = {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    passwordHash: string;
    role: "STUDENT" | "ADMIN";
}

export type User = {
    firstName: string;
    lastName: string;
    email: string;
    passwordHash: string;
    role: "STUDENT" | "ADMIN";
}

export type UserEntry = {
    user: User;
    url: number;
}